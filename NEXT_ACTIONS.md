# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 52/55 (94.5%)
- **Function parity:** 1071/1083 matched (target 3383) — 98.9%
- **Class/type parity:** 108/121 matched (target 823) — 89.3%
- **Combined symbol parity:** 1179/1204 matched (target 4206) — 97.9%
- **Average inline-code cosine:** 0.56 (function body across 52 matched files)
- **Average documentation cosine:** 0.30 (doc text across 52 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 23 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. token
- **Similarity:** 0.67 (needs 18% improvement)
- **Dependencies:** 17
- **Priority Score:** 17002204.0
- **Functions:** 17/17 matched (target 685)
- **Missing functions:** _none_
- **Types:** 5/5 matched (target 305)
- **Missing types:** _none_
- **Action:** Review and complete missing sections

### 2. punctuated
- **Similarity:** 0.48 (needs 37% improvement)
- **Dependencies:** 13
- **Priority Score:** 13006805.0
- **Functions:** 54/54 matched (target 340)
- **Missing functions:** _none_
- **Types:** 14/14 matched (target 36)
- **Missing types:** _none_
- **Action:** Deep review - likely missing major functionality

### 3. expr
- **Similarity:** 0.47 (needs 38% improvement)
- **Dependencies:** 10
- **Priority Score:** 10006605.0
- **Functions:** 65/65 matched (target 238)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 57)
- **Missing types:** _none_
- **Action:** Deep review - likely missing major functionality

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. token

- **Target:** `token.Token`
- **Similarity:** 0.67
- **Dependents:** 17
- **Priority Score:** 17002204.0
- **Functions:** 17/17 matched (target 685)
- **Missing functions:** _none_
- **Types:** 5/5 matched (target 305)
- **Missing types:** _none_

### 2. ident

- **Target:** `syn.Ident`
- **Similarity:** 0.88
- **Dependents:** 14
- **Priority Score:** 14000601.0
- **Functions:** 6/6 matched (target 21)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 3)
- **Missing types:** _none_

### 3. punctuated

- **Target:** `syn.Punctuated`
- **Similarity:** 0.48
- **Dependents:** 13
- **Priority Score:** 13006805.0
- **Functions:** 54/54 matched (target 340)
- **Missing functions:** _none_
- **Types:** 14/14 matched (target 36)
- **Missing types:** _none_

### 4. expr

- **Target:** `syn.Expr`
- **Similarity:** 0.47
- **Dependents:** 10
- **Priority Score:** 10006605.0
- **Functions:** 65/65 matched (target 238)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 57)
- **Missing types:** _none_

### 5. path

- **Target:** `syn.Path`
- **Similarity:** 0.76
- **Dependents:** 9
- **Priority Score:** 9012802.0
- **Functions:** 25/26 matched (target 66)
- **Missing functions:** `clone`
- **Types:** 2/2 matched (target 28)
- **Missing types:** _none_
- **Lint issues:** 1

### 6. lifetime

- **Target:** `syn.Lifetime`
- **Similarity:** 0.63
- **Dependents:** 8
- **Priority Score:** 8011203.5
- **Functions:** 10/11 matched (target 17)
- **Missing functions:** `clone`
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_

### 7. verbatim

- **Target:** `syn.Verbatim`
- **Similarity:** 0.49
- **Dependents:** 7
- **Priority Score:** 7000105.0
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 8. lit

- **Target:** `syn.Lit`
- **Similarity:** 0.56
- **Dependents:** 5
- **Priority Score:** 5004204.5
- **Functions:** 38/38 matched (target 146)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 36)
- **Missing types:** _none_

### 9. lookahead

- **Target:** `syn.Lookahead`
- **Similarity:** 0.73
- **Dependents:** 5
- **Priority Score:** 5001402.5
- **Functions:** 8/8 matched (target 24)
- **Missing functions:** _none_
- **Types:** 6/6 matched (target 13)
- **Missing types:** _none_

### 10. span

- **Target:** `syn.Span`
- **Similarity:** 0.31
- **Dependents:** 4
- **Priority Score:** 4000206.8
- **Functions:** 1/1 matched (target 11)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 11. error

- **Target:** `syn.Error`
- **Similarity:** 0.46
- **Dependents:** 3
- **Priority Score:** 3012205.5
- **Functions:** 13/14 matched (target 39)
- **Missing functions:** `from`
- **Types:** 8/8 matched (target 12)
- **Missing types:** _none_

### 12. classify

- **Target:** `syn.Classify`
- **Similarity:** 0.70
- **Dependents:** 3
- **Priority Score:** 3000903.0
- **Functions:** 9/9 matched (target 12)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 4)
- **Missing types:** _none_

### 13. precedence

- **Target:** `syn.Precedence`
- **Similarity:** 0.66
- **Dependents:** 2
- **Priority Score:** 2010703.4
- **Functions:** 5/6 matched (target 5)
- **Missing functions:** `clone`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 14. item

- **Target:** `syn.Item`
- **Similarity:** 0.57
- **Dependents:** 2
- **Priority Score:** 2003204.4
- **Functions:** 28/28 matched (target 131)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 65)
- **Missing types:** _none_
- **Lint issues:** 1

### 15. generics

- **Target:** `syn.Generics`
- **Similarity:** 0.67
- **Dependents:** 2
- **Priority Score:** 2002503.2
- **Functions:** 15/15 matched (target 93)
- **Missing functions:** _none_
- **Types:** 10/10 matched (target 34)
- **Missing types:** _none_
- **Lint issues:** 1

### 16. pat

- **Target:** `syn.Pat`
- **Similarity:** 0.79
- **Dependents:** 2
- **Priority Score:** 2002402.1
- **Functions:** 23/23 matched (target 65)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 22)
- **Missing types:** _none_

### 17. gen.fold

- **Target:** `gen.Fold`
- **Similarity:** 0.27
- **Dependents:** 1
- **Priority Score:** 1019107.3
- **Functions:** 190/190 matched (target 197)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 18. gen.visit_mut

- **Target:** `gen.VisitMut`
- **Similarity:** 0.24
- **Dependents:** 1
- **Priority Score:** 1019007.6
- **Functions:** 189/189 matched (target 318)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Lint issues:** 24

### 19. gen.visit

- **Target:** `gen.Visit`
- **Similarity:** 0.26
- **Dependents:** 1
- **Priority Score:** 1018907.4
- **Functions:** 188/188 matched (target 201)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Lint issues:** 15

### 20. attr

- **Target:** `syn.Attr`
- **Similarity:** 0.67
- **Dependents:** 1
- **Priority Score:** 1002703.3
- **Functions:** 23/23 matched (target 49)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 17)
- **Missing types:** _none_
- **Lint issues:** 1

### 21. group

- **Target:** `syn.Group`
- **Similarity:** 0.89
- **Dependents:** 1
- **Priority Score:** 1000901.1
- **Functions:** 5/5 matched (target 8)
- **Missing functions:** _none_
- **Types:** 4/4 matched
- **Missing types:** _none_

### 22. ty

- **Target:** `syn.Type`
- **Similarity:** 0.52
- **Dependents:** 1
- **Priority Score:** 1000704.8
- **Functions:** 7/7 matched (target 56)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 24)
- **Missing types:** _none_
- **Lint issues:** 1

### 23. bigint

- **Target:** `syn.BigInt`
- **Similarity:** 0.67
- **Dependents:** 1
- **Priority Score:** 1000603.3
- **Functions:** 5/5 matched (target 8)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 24. derive

- **Target:** `syn.Derive`
- **Similarity:** 0.72
- **Dependents:** 1
- **Priority Score:** 1000502.8
- **Functions:** 5/5 matched (target 15)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 13)
- **Missing types:** _none_

### 25. scan_expr

- **Target:** `syn.ScanExpr`
- **Similarity:** 0.58
- **Dependents:** 1
- **Priority Score:** 1000304.2
- **Functions:** 1/1 matched (target 87)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_
- **Lint issues:** 1

### 26. spanned

- **Target:** `syn.Spanned`
- **Similarity:** 0.97
- **Dependents:** 1
- **Priority Score:** 1000300.3
- **Functions:** 1/1 matched (target 3)
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_

### 27. gen.debug

- **Target:** `gen.Debug`
- **Similarity:** 0.02
- **Dependents:** 1
- **Priority Score:** 1000209.8
- **Functions:** 2/2 matched (target 28)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 28. file

- **Target:** `syn.File`
- **Similarity:** 0.69
- **Dependents:** 1
- **Priority Score:** 1000203.1
- **Functions:** 2/2 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 29. gen.clone

- **Target:** `gen.Clone`
- **Similarity:** 0.76
- **Dependents:** 1
- **Priority Score:** 1000102.4
- **Functions:** 1/1 matched (target 190)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 30. sealed

- **Target:** `syn.Sealed`
- **Similarity:** 1.00
- **Dependents:** 1
- **Priority Score:** 1000100.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 31. parse

- **Target:** `syn.Parse`
- **Similarity:** 0.49
- **Dependents:** 0
- **Priority Score:** 54205.1
- **Functions:** 31/33 matched (target 56)
- **Missing functions:** `call`, `tokens_to_parse_buffer`
- **Types:** 6/9 matched (target 16)
- **Missing types:** `Parse`, `Parser`, `Output`
- **Lint issues:** 2

### 32. export

- **Target:** `syn.Export`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 50800.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 3/8 matched (target 4)
- **Missing types:** `bool`, `str`, `Span`, `TokenStream`, `private`

### 33. parse_quote

- **Target:** `syn.ParseQuote`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 20210.0
- **Functions:** 0/1 matched (target 7)
- **Missing functions:** `parse`
- **Types:** 0/1 matched (target 0)
- **Missing types:** `ParseQuote`

### 34. buffer

- **Target:** `syn.Buffer`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 3502.7
- **Functions:** 31/31 matched (target 33)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 10)
- **Missing types:** _none_

### 35. fixup

- **Target:** `syn.Fixup`
- **Similarity:** 0.65
- **Dependents:** 0
- **Priority Score:** 1703.5
- **Functions:** 15/15 matched (target 24)
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Lint issues:** 1

### 36. data

- **Target:** `syn.Data`
- **Similarity:** 0.62
- **Dependents:** 0
- **Priority Score:** 1403.8
- **Functions:** 11/11 matched (target 23)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 14)
- **Missing types:** _none_
- **Lint issues:** 1

### 37. ext

- **Target:** `syn.Ext`
- **Similarity:** 0.82
- **Dependents:** 0
- **Priority Score:** 1401.8
- **Functions:** 7/7 matched (target 12)
- **Missing functions:** _none_
- **Types:** 7/7 matched (target 8)
- **Missing types:** _none_

### 38. stmt

- **Target:** `syn.Stmt`
- **Similarity:** 0.64
- **Dependents:** 0
- **Priority Score:** 803.6
- **Functions:** 7/7 matched (target 20)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 9)
- **Missing types:** _none_

### 39. mac

- **Target:** `syn.Mac`
- **Similarity:** 0.70
- **Dependents:** 0
- **Priority Score:** 803.0
- **Functions:** 8/8 matched (target 10)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 40. meta

- **Target:** `syn.Meta`
- **Similarity:** 0.66
- **Dependents:** 0
- **Priority Score:** 603.4
- **Functions:** 5/5 matched (target 7)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 41. thread

- **Target:** `syn.Thread`
- **Similarity:** 0.44
- **Dependents:** 0
- **Priority Score:** 505.6
- **Functions:** 4/4 matched (target 7)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 42. discouraged

- **Target:** `syn.Discouraged`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 407.0
- **Functions:** 2/2 matched (target 6)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 4)
- **Missing types:** _none_

### 43. tt

- **Target:** `syn.Tt`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 404.8
- **Functions:** 2/2 matched (target 17)
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_

### 44. restriction

- **Target:** `syn.Restriction`
- **Similarity:** 0.64
- **Dependents:** 0
- **Priority Score:** 403.6
- **Functions:** 4/4 matched (target 14)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 12)
- **Missing types:** _none_
- **Lint issues:** 2

### 45. op

- **Target:** `syn.Op`
- **Similarity:** 0.68
- **Dependents:** 0
- **Priority Score:** 203.2
- **Functions:** 2/2 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 35)
- **Missing types:** _none_

### 46. print

- **Target:** `syn.Print`
- **Similarity:** 0.86
- **Dependents:** 0
- **Priority Score:** 201.4
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 47. whitespace

- **Target:** `syn.Whitespace`
- **Similarity:** 0.88
- **Dependents:** 0
- **Priority Score:** 201.2
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 48. gen.eq

- **Target:** `gen.Eq`
- **Similarity:** 0.10
- **Dependents:** 0
- **Priority Score:** 109.0
- **Functions:** 1/1 matched (target 20)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 49. gen.hash

- **Target:** `gen.Hash`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 108.5
- **Functions:** 1/1 matched (target 45)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 50. custom_keyword

- **Target:** `syn.CustomKeyword [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 51. parse_macro_input

- **Target:** `syn.ParseMacroInput [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 2)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 3)
- **Missing types:** _none_

### 52. custom_punctuation

- **Target:** `syn.CustomPunctuation [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 9)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 3)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

