# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 53/55 (96.4%)
- **Function parity:** 2703/3015 matched (target 5078) — 89.7%
- **Class/type parity:** 102/120 matched (target 877) — 85.0%
- **Combined symbol parity:** 2805/3135 matched (target 5955) — 89.5%
- **Average inline-code cosine:** 0.20 (function body across 53 matched files)
- **Average documentation cosine:** 0.45 (doc text across 53 matched files)
- **Cheat-zeroed Files:** 4
- **Critical Issues:** 51 files with <0.60 function similarity
- **Needs Review:** 0 files with 0.60-0.84 function similarity
- **Excellent:** 2 files with >=0.85 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. token
- **Similarity:** 0.16 (needs 69% improvement)
- **Dependencies:** 17
- **Priority Score:** 17033408.0
- **Functions:** 26/29 matched (target 691)
- **Missing functions:** `format_token`, `punct`, `keyword`
- **Types:** 5/5 matched (target 308)
- **Missing types:** _none_
- **Symbol Deficit:** 3 (functions: 3, types: 0)
- **Action:** Deep review - likely missing major functionality

### 2. ident
- **Similarity:** 0.29 (needs 56% improvement)
- **Dependencies:** 15
- **Priority Score:** 15000607.0
- **Functions:** 6/6 matched (target 20)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Action:** Deep review - likely missing major functionality

### 3. punctuated
- **Similarity:** 0.09 (needs 76% improvement)
- **Dependencies:** 13
- **Priority Score:** 13030909.0
- **Functions:** 93/95 matched (target 370)
- **Missing functions:** `into_iter`, `into_iter`
- **Types:** 14/14 matched (target 37)
- **Missing types:** _none_
- **Symbol Deficit:** 2 (functions: 2, types: 0)
- **Action:** Deep review - likely missing major functionality

### 4. expr
- **Similarity:** 0.24 (needs 61% improvement)
- **Dependencies:** 10
- **Priority Score:** 10446108.0
- **Functions:** 117/160 matched (target 240)
- **Missing functions:** `from`, `eq`, `hash`, `fmt`, `span`, `parse_with_earlier_boundary_rule`, `parse_expr`, `unary_expr`, `trailer_expr`, `atom_expr`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `outer_attrs_to_tokens`
- **Types:** 1/1 matched (target 57)
- **Missing types:** _none_
- **Symbol Deficit:** 43 (functions: 43, types: 0)
- **Action:** Deep review - likely missing major functionality

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. token

- **Target:** `token.Token`
- **Similarity:** 0.16
- **Dependents:** 17
- **Priority Score:** 17033408.0
- **Functions:** 26/29 matched (target 691)
- **Missing functions:** `format_token`, `punct`, `keyword`
- **Types:** 5/5 matched (target 308)
- **Missing types:** _none_

### 2. ident

- **Target:** `syn.Ident`
- **Similarity:** 0.29
- **Dependents:** 15
- **Priority Score:** 15000607.0
- **Functions:** 6/6 matched (target 20)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 3. punctuated

- **Target:** `syn.Punctuated`
- **Similarity:** 0.09
- **Dependents:** 13
- **Priority Score:** 13030909.0
- **Functions:** 93/95 matched (target 370)
- **Missing functions:** `into_iter`, `into_iter`
- **Types:** 14/14 matched (target 37)
- **Missing types:** _none_

### 4. expr

- **Target:** `syn.Expr`
- **Similarity:** 0.24
- **Dependents:** 10
- **Priority Score:** 10446108.0
- **Functions:** 117/160 matched (target 240)
- **Missing functions:** `from`, `eq`, `hash`, `fmt`, `span`, `parse_with_earlier_boundary_rule`, `parse_expr`, `unary_expr`, `trailer_expr`, `atom_expr`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `outer_attrs_to_tokens`
- **Types:** 1/1 matched (target 57)
- **Missing types:** _none_

### 5. path

- **Target:** `syn.Path`
- **Similarity:** 0.32
- **Dependents:** 9
- **Priority Score:** 9034407.0
- **Functions:** 39/42 matched (target 67)
- **Missing functions:** `type_as_named_arg`, `to_tokens`, `to_tokens`
- **Types:** 2/2 matched (target 28)
- **Missing types:** _none_

### 6. lifetime

- **Target:** `syn.Lifetime`
- **Similarity:** 0.23
- **Dependents:** 8
- **Priority Score:** 8001407.5
- **Functions:** 13/13 matched (target 20)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_

### 7. verbatim

- **Target:** `syn.Verbatim`
- **Similarity:** 0.43
- **Dependents:** 8
- **Priority Score:** 8000105.5
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 8. lit

- **Target:** `syn.Lit`
- **Similarity:** 0.15
- **Dependents:** 5
- **Priority Score:** 5111908.5
- **Functions:** 106/116 matched (target 197)
- **Missing functions:** `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `parse_lit_byte`
- **Types:** 3/3 matched (target 45)
- **Missing types:** _none_

### 9. lookahead

- **Target:** `syn.Lookahead`
- **Similarity:** 0.22
- **Dependents:** 5
- **Priority Score:** 5001508.0
- **Functions:** 9/9 matched (target 24)
- **Missing functions:** _none_
- **Types:** 6/6 matched (target 13)
- **Missing types:** _none_

### 10. classify

- **Target:** `syn.Classify`
- **Similarity:** 0.09
- **Dependents:** 3
- **Priority Score:** 3041109.0
- **Functions:** 7/11 matched
- **Missing functions:** `last_type_in_path`, `last_type_in_bounds`, `last_type_in_path`, `last_type_in_bounds`
- **Types:** 0/0 matched (target 3)
- **Missing types:** _none_

### 11. span

- **Target:** `syn.Span`
- **Similarity:** 0.06
- **Dependents:** 3
- **Priority Score:** 3001009.5
- **Functions:** 9/9 matched (target 12)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 13)
- **Missing types:** _none_

### 12. gen.debug

- **Target:** `gen.Debug`
- **Similarity:** 0.04
- **Dependents:** 1
- **Priority Score:** 2909509.8
- **Functions:** 107/295 matched (target 354)
- **Missing functions:** `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 13. error

- **Target:** `syn.Error`
- **Similarity:** 0.18
- **Dependents:** 2
- **Priority Score:** 2083408.1
- **Functions:** 20/26 matched (target 43)
- **Missing functions:** `new`, `new2`, `fmt`, `clone`, `clone`, `into_iter`
- **Types:** 6/8 matched (target 12)
- **Missing types:** `Result`, `Error`
- **Lint issues:** 2

### 14. item

- **Target:** `syn.Item`
- **Similarity:** 0.21
- **Dependents:** 2
- **Priority Score:** 2022107.9
- **Functions:** 117/118 matched (target 181)
- **Missing functions:** `clone`
- **Types:** 3/3 matched (target 69)
- **Missing types:** _none_

### 15. generics

- **Target:** `syn.Generics`
- **Similarity:** 0.20
- **Dependents:** 2
- **Priority Score:** 2005908.0
- **Functions:** 49/49 matched (target 109)
- **Missing functions:** _none_
- **Types:** 10/10 matched (target 39)
- **Missing types:** _none_

### 16. pat

- **Target:** `syn.Pat`
- **Similarity:** 0.27
- **Dependents:** 2
- **Priority Score:** 2003707.2
- **Functions:** 36/36 matched (target 73)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 24)
- **Missing types:** _none_

### 17. precedence

- **Target:** `syn.Precedence`
- **Similarity:** 0.12
- **Dependents:** 2
- **Priority Score:** 2000708.8
- **Functions:** 6/6 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 18. ty

- **Target:** `syn.Type`
- **Similarity:** 0.24
- **Dependents:** 1
- **Priority Score:** 1175107.6
- **Functions:** 34/51 matched (target 81)
- **Missing functions:** `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`
- **Types:** 0/0 matched (target 28)
- **Missing types:** _none_

### 19. gen.fold

- **Target:** `gen.Fold`
- **Similarity:** 0.17
- **Dependents:** 1
- **Priority Score:** 1038008.3
- **Functions:** 379/379 matched (target 395)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 20. gen.visit_mut

- **Target:** `gen.VisitMut`
- **Similarity:** 0.14
- **Dependents:** 1
- **Priority Score:** 1037908.6
- **Functions:** 378/378 matched (target 516)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 21. gen.visit

- **Target:** `gen.Visit`
- **Similarity:** 0.16
- **Dependents:** 1
- **Priority Score:** 1037808.4
- **Functions:** 377/377 matched (target 402)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 22. gen.clone

- **Target:** `gen.Clone`
- **Similarity:** 0.19
- **Dependents:** 1
- **Priority Score:** 1018808.1
- **Functions:** 188/188 matched (target 218)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 23. attr

- **Target:** `syn.Attr`
- **Similarity:** 0.19
- **Dependents:** 1
- **Priority Score:** 1014008.1
- **Functions:** 35/36 matched (target 48)
- **Missing functions:** `fmt`
- **Types:** 4/4 matched (target 17)
- **Missing types:** _none_

### 24. spanned

- **Target:** `syn.Spanned`
- **Similarity:** 0.09
- **Dependents:** 1
- **Priority Score:** 1010309.1
- **Functions:** 1/1 matched (target 4)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Sealed`
- **Lint issues:** 1

### 25. sealed

- **Target:** `syn.Sealed`
- **Similarity:** 1.00
- **Dependents:** 1
- **Priority Score:** 1010100.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/1 matched
- **Missing types:** `Sealed`

### 26. group

- **Target:** `syn.Group`
- **Similarity:** 0.47
- **Dependents:** 1
- **Priority Score:** 1000905.3
- **Functions:** 5/5 matched (target 8)
- **Missing functions:** _none_
- **Types:** 4/4 matched
- **Missing types:** _none_

### 27. bigint

- **Target:** `syn.BigInt`
- **Similarity:** 0.19
- **Dependents:** 1
- **Priority Score:** 1000608.1
- **Functions:** 5/5 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 28. derive

- **Target:** `syn.Derive`
- **Similarity:** 0.21
- **Dependents:** 1
- **Priority Score:** 1000507.9
- **Functions:** 5/5 matched (target 18)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 13)
- **Missing types:** _none_

### 29. scan_expr

- **Target:** `syn.ScanExpr`
- **Similarity:** 0.21
- **Dependents:** 1
- **Priority Score:** 1000307.9
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 23)
- **Missing types:** _none_

### 30. parse_quote

- **Target:** `syn.ParseQuote`
- **Similarity:** 0.01
- **Dependents:** 0
- **Priority Score:** 101209.9
- **Functions:** 1/11 matched (target 12)
- **Missing functions:** `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 31. parse

- **Target:** `syn.Parse`
- **Similarity:** 0.16
- **Dependents:** 0
- **Priority Score:** 96008.4
- **Functions:** 43/51 matched (target 61)
- **Missing functions:** `clone`, `peek2`, `peek3`, `__parse_scoped`, `parse_scoped`, `parse`, `clone`, `fmt`
- **Types:** 8/9 matched (target 17)
- **Missing types:** `Output`

### 32. export

- **Target:** `syn.Export`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 60800.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 2/8 matched (target 3)
- **Missing types:** `bool`, `str`, `Span`, `TokenStream2`, `TokenStream`, `private`

### 33. gen.eq

- **Target:** `gen.Eq`
- **Similarity:** 0.16
- **Dependents:** 0
- **Priority Score:** 58708.4
- **Functions:** 183/187 matched (target 229)
- **Missing functions:** `eq`, `eq`, `eq`, `eq`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 34. ext

- **Target:** `syn.Ext`
- **Similarity:** 0.18
- **Dependents:** 0
- **Priority Score:** 41408.2
- **Functions:** 5/7 matched (target 11)
- **Missing functions:** `append`, `new_spanned`
- **Types:** 5/7 matched (target 6)
- **Missing types:** `Token`, `Sealed`

### 35. data

- **Target:** `syn.Data`
- **Similarity:** 0.16
- **Dependents:** 0
- **Priority Score:** 22308.4
- **Functions:** 18/20 matched (target 34)
- **Missing functions:** `into_iter`, `into_iter`
- **Types:** 3/3 matched (target 15)
- **Missing types:** _none_

### 36. gen.hash

- **Target:** `gen.Hash`
- **Similarity:** 0.22
- **Dependents:** 0
- **Priority Score:** 18707.8
- **Functions:** 187/187 matched (target 415)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 37. fixup

- **Target:** `syn.Fixup`
- **Similarity:** 0.19
- **Dependents:** 0
- **Priority Score:** 11808.1
- **Functions:** 15/16 matched (target 24)
- **Missing functions:** `clone`
- **Types:** 2/2 matched
- **Missing types:** _none_

### 38. meta

- **Target:** `syn.Meta`
- **Similarity:** 0.21
- **Dependents:** 0
- **Priority Score:** 10707.9
- **Functions:** 5/6 matched (target 7)
- **Missing functions:** `parse_nested_meta`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Lint issues:** 2

### 39. discouraged

- **Target:** `syn.Discouraged`
- **Similarity:** 0.01
- **Dependents:** 0
- **Priority Score:** 10509.9
- **Functions:** 2/2 matched (target 4)
- **Missing functions:** _none_
- **Types:** 2/3 matched
- **Missing types:** `Sealed`

### 40. restriction

- **Target:** `syn.Restriction`
- **Similarity:** 0.11
- **Dependents:** 0
- **Priority Score:** 10508.9
- **Functions:** 4/5 matched (target 11)
- **Missing functions:** `to_tokens`
- **Types:** 0/0 matched (target 9)
- **Missing types:** _none_

### 41. buffer

- **Target:** `syn.Buffer`
- **Similarity:** 0.22
- **Dependents:** 0
- **Priority Score:** 3707.8
- **Functions:** 33/33 matched (target 35)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 10)
- **Missing types:** _none_

### 42. stmt

- **Target:** `syn.Stmt`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 1408.5
- **Functions:** 13/13 matched (target 31)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 11)
- **Missing types:** _none_

### 43. mac

- **Target:** `syn.Mac`
- **Similarity:** 0.19
- **Dependents:** 0
- **Priority Score:** 808.1
- **Functions:** 8/8 matched (target 10)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 44. tt

- **Target:** `syn.Tt`
- **Similarity:** 0.09
- **Dependents:** 0
- **Priority Score:** 609.1
- **Functions:** 4/4 matched (target 17)
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_

### 45. thread

- **Target:** `syn.Thread`
- **Similarity:** 0.10
- **Dependents:** 0
- **Priority Score:** 509.0
- **Functions:** 4/4 matched (target 5)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 46. op

- **Target:** `syn.Op`
- **Similarity:** 0.22
- **Dependents:** 0
- **Priority Score:** 407.8
- **Functions:** 4/4 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 35)
- **Missing types:** _none_

### 47. file

- **Target:** `syn.File`
- **Similarity:** 0.11
- **Dependents:** 0
- **Priority Score:** 208.9
- **Functions:** 2/2 matched (target 9)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 3)
- **Missing types:** _none_

### 48. whitespace

- **Target:** `syn.Whitespace`
- **Similarity:** 0.16
- **Dependents:** 0
- **Priority Score:** 208.4
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 49. print

- **Target:** `syn.Print`
- **Similarity:** 0.23
- **Dependents:** 0
- **Priority Score:** 207.7
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 50. macros

- **Target:** `syn.Macros [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 9)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Lint issues:** 1

### 51. custom_keyword

- **Target:** `syn.CustomKeyword [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 9)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 52. parse_macro_input

- **Target:** `syn.ParseMacroInput [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 5)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 4)
- **Missing types:** _none_
- **Lint issues:** 1

### 53. custom_punctuation

- **Target:** `syn.CustomPunctuation [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 17)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 4)
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

