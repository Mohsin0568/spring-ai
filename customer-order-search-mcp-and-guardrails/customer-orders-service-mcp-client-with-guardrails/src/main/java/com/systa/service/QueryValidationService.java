package com.systa.service;

import com.systa.exception.GuardrailViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class QueryValidationService {

    private static final Logger logger = LoggerFactory.getLogger(QueryValidationService.class);
    private static final int MAX_QUERY_LENGTH = 500;

    // Matches write/destructive operation keywords as whole words (case-insensitive).
    // Word boundaries prevent matching substrings — e.g. \bdelete\b won't match "deleted".
    private static final List<Pattern> FORBIDDEN_KEYWORD_PATTERNS = List.of(
        Pattern.compile("\\bdelete\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bdrop\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\btruncate\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\binsert\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bupdate\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bmodify\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\balter\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bgrant\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\brevoke\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bexecute\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bremove\\b", Pattern.CASE_INSENSITIVE)
    );

    // Common prompt injection attack patterns.
    private static final List<Pattern> INJECTION_PATTERNS = List.of(
        Pattern.compile("ignore.{0,20}(previous|all|prior).{0,20}instructions?", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
        Pattern.compile("forget.{0,20}(your|all|previous).{0,20}instructions?", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
        Pattern.compile("\\bact\\s+as\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\byou\\s+are\\s+now\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bpretend\\s+(you\\s+are|to\\s+be)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bnew\\s+(instruction|task|role|prompt)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\[\\s*(SYSTEM|INST|SYS)\\s*\\]", Pattern.CASE_INSENSITIVE),
        Pattern.compile("```\\s*system", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bjailbreak\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bbypass\\b.{0,30}\\b(guardrail|restriction|rule|filter)", Pattern.CASE_INSENSITIVE)
    );

    // PII patterns — credit cards (Visa/MC/AmEx/Discover), SSN, and IBAN bank account numbers.
    private static final List<Pattern> PII_PATTERNS = List.of(
        // Visa: 16 digits starting with 4, optional space/dash separators
        Pattern.compile("\\b4[0-9]{3}[\\s\\-]?[0-9]{4}[\\s\\-]?[0-9]{4}[\\s\\-]?[0-9]{4}\\b"),
        // MasterCard: 16 digits starting with 51-55
        Pattern.compile("\\b5[1-5][0-9]{2}[\\s\\-]?[0-9]{4}[\\s\\-]?[0-9]{4}[\\s\\-]?[0-9]{4}\\b"),
        // American Express: 15 digits starting with 34 or 37
        Pattern.compile("\\b3[47][0-9]{2}[\\s\\-]?[0-9]{6}[\\s\\-]?[0-9]{5}\\b"),
        // Discover: 16 digits starting with 6011 or 65
        Pattern.compile("\\b6(?:011|5[0-9]{2})[\\s\\-]?[0-9]{4}[\\s\\-]?[0-9]{4}[\\s\\-]?[0-9]{4}\\b"),
        // SSN: 123-45-6789 (formatted only — bare 9 digits overlap with order IDs)
        Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b"),
        // IBAN: 2 letter country code + 2 check digits + up to 30 alphanumeric BBAN
        Pattern.compile("\\b[A-Z]{2}\\d{2}[A-Z0-9]{4}\\d{7}[A-Z0-9]{0,16}\\b")
    );

    public void validate(String query) {
        if (query == null || query.isBlank()) {
            throw new GuardrailViolationException("Query must not be empty.");
        }

        if (query.length() > MAX_QUERY_LENGTH) {
            throw new GuardrailViolationException(
                "Query exceeds maximum allowed length of " + MAX_QUERY_LENGTH + " characters.");
        }

        for (Pattern pattern : FORBIDDEN_KEYWORD_PATTERNS) {
            if (pattern.matcher(query).find()) {
                logger.warn("Guardrail triggered — forbidden keyword detected in query: [{}]", query);
                throw new GuardrailViolationException(
                    "Query contains a forbidden operation. Only read and search operations are permitted.");
            }
        }

        for (Pattern pattern : INJECTION_PATTERNS) {
            if (pattern.matcher(query).find()) {
                logger.warn("Guardrail triggered — prompt injection pattern detected in query: [{}]", query);
                throw new GuardrailViolationException(
                    "Query contains a disallowed pattern. Please submit a valid order search query.");
            }
        }

        for (Pattern pattern : PII_PATTERNS) {
            if (pattern.matcher(query).find()) {
                logger.warn("Guardrail triggered — PII detected in query (redacted for logging)");
                throw new GuardrailViolationException(
                    "Query contains sensitive personal information (PII). Please remove financial or personal identifiers and search by order ID or customer name instead.");
            }
        }
    }
}
