# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 53/55 (96.4%)
- **Function parity:** 1081/1083 matched (target 3184) — 99.8%
- **Class/type parity:** 96/121 matched (target 817) — 79.3%
- **Combined symbol parity:** 1177/1204 matched (target 4001) — 97.8%
- **Average inline-code cosine:** 0.55 (function body across 53 matched files)
- **Average documentation cosine:** 0.32 (doc text across 53 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 26 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. token
- **Similarity:** 0.64 (needs 21% improvement)
- **Dependencies:** 17
- **Priority Score:** 17012204.0
- **Functions:** 16/17 matched (target 684)
- **Missing functions:** `Group`
- **Types:** 5/5 matched (target 305)
- **Missing types:** _none_
- **Symbol Deficit:** 1 (functions: 1, types: 0)
- **Action:** Review and complete missing sections

### 2. punctuated
- **Similarity:** 0.47 (needs 38% improvement)
- **Dependencies:** 13
- **Priority Score:** 13026805.0
- **Functions:** 54/54 matched (target 301)
- **Missing functions:** _none_
- **Types:** 12/14 matched (target 34)
- **Missing types:** `Item`, `Output`
- **Symbol Deficit:** 2 (functions: 0, types: 2)
- **Action:** Deep review - likely missing major functionality

### 3. expr
- **Similarity:** 0.47 (needs 38% improvement)
- **Dependencies:** 10
- **Priority Score:** 10006605.0
- **Functions:** 65/65 matched (target 213)
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
- **Similarity:** 0.64
- **Dependents:** 17
- **Priority Score:** 17012204.0
- **Functions:** 16/17 matched (target 684)
- **Missing functions:** `Group`
- **Types:** 5/5 matched (target 305)
- **Missing types:** _none_

### 2. ident

- **Target:** `syn.Ident`
- **Similarity:** 0.88
- **Dependents:** 14
- **Priority Score:** 14000601.0
- **Functions:** 6/6 matched (target 20)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 3)
- **Missing types:** _none_

### 3. punctuated

- **Target:** `syn.Punctuated`
- **Similarity:** 0.47
- **Dependents:** 13
- **Priority Score:** 13026805.0
- **Functions:** 54/54 matched (target 301)
- **Missing functions:** _none_
- **Types:** 12/14 matched (target 34)
- **Missing types:** `Item`, `Output`

### 4. expr

- **Target:** `syn.Expr`
- **Similarity:** 0.47
- **Dependents:** 10
- **Priority Score:** 10006605.0
- **Functions:** 65/65 matched (target 213)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 57)
- **Missing types:** _none_

### 5. path

- **Target:** `syn.Path`
- **Similarity:** 0.74
- **Dependents:** 9
- **Priority Score:** 9012803.0
- **Functions:** 26/26 matched (target 65)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 27)
- **Missing types:** `QSelfDelimiters`
- **Lint issues:** 1

### 6. lifetime

- **Target:** `syn.Lifetime`
- **Similarity:** 0.65
- **Dependents:** 8
- **Priority Score:** 8001203.5
- **Functions:** 11/11 matched (target 18)
- **Missing functions:** _none_
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

### 8. lookahead

- **Target:** `syn.Lookahead`
- **Similarity:** 0.73
- **Dependents:** 5
- **Priority Score:** 5011402.5
- **Functions:** 8/8 matched (target 24)
- **Missing functions:** _none_
- **Types:** 5/6 matched (target 12)
- **Missing types:** `Token`

### 9. lit

- **Target:** `syn.Lit`
- **Similarity:** 0.56
- **Dependents:** 5
- **Priority Score:** 5004204.5
- **Functions:** 38/38 matched (target 139)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 36)
- **Missing types:** _none_

### 10. span

- **Target:** `syn.Span`
- **Similarity:** 0.31
- **Dependents:** 4
- **Priority Score:** 4000206.8
- **Functions:** 1/1 matched (target 12)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 11. error

- **Target:** `syn.Error`
- **Similarity:** 0.50
- **Dependents:** 3
- **Priority Score:** 3022205.0
- **Functions:** 14/14 matched (target 40)
- **Missing functions:** _none_
- **Types:** 6/8 matched (target 10)
- **Missing types:** `_Test`, `Item`

### 12. classify

- **Target:** `syn.Classify`
- **Similarity:** 0.70
- **Dependents:** 3
- **Priority Score:** 3000903.0
- **Functions:** 9/9 matched (target 12)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 4)
- **Missing types:** _none_

### 13. generics

- **Target:** `syn.Generics`
- **Similarity:** 0.67
- **Dependents:** 2
- **Priority Score:** 2012503.2
- **Functions:** 15/15 matched (target 93)
- **Missing functions:** _none_
- **Types:** 9/10 matched (target 33)
- **Missing types:** `Item`
- **Lint issues:** 1

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

### 15. pat

- **Target:** `syn.Pat`
- **Similarity:** 0.79
- **Dependents:** 2
- **Priority Score:** 2002402.1
- **Functions:** 23/23 matched (target 65)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 22)
- **Missing types:** _none_

### 16. precedence

- **Target:** `syn.Precedence`
- **Similarity:** 0.80
- **Dependents:** 2
- **Priority Score:** 2000702.0
- **Functions:** 6/6 matched (target 7)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 17. scan_expr

- **Target:** `syn.ScanExpr`
- **Similarity:** 0.58
- **Dependents:** 1
- **Priority Score:** 1020304.2
- **Functions:** 1/1 matched (target 87)
- **Missing functions:** _none_
- **Types:** 0/2 matched (target 3)
- **Missing types:** `Input`, `Action`
- **Lint issues:** 1

### 18. gen.fold

- **Target:** `gen.Fold`
- **Similarity:** 0.27
- **Dependents:** 1
- **Priority Score:** 1019107.3
- **Functions:** 190/190 matched (target 197)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 19. gen.visit_mut

- **Target:** `gen.VisitMut`
- **Similarity:** 0.24
- **Dependents:** 1
- **Priority Score:** 1019007.6
- **Functions:** 189/189 matched (target 318)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 20. gen.visit

- **Target:** `gen.Visit`
- **Similarity:** 0.26
- **Dependents:** 1
- **Priority Score:** 1018907.4
- **Functions:** 188/188 matched (target 201)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Lint issues:** 15

### 21. attr

- **Target:** `syn.Attr`
- **Similarity:** 0.71
- **Dependents:** 1
- **Priority Score:** 1012702.9
- **Functions:** 23/23 matched (target 50)
- **Missing functions:** _none_
- **Types:** 3/4 matched (target 16)
- **Missing types:** `Ret`
- **Lint issues:** 1

### 22. group

- **Target:** `syn.Group`
- **Similarity:** 0.89
- **Dependents:** 1
- **Priority Score:** 1000901.1
- **Functions:** 5/5 matched (target 8)
- **Missing functions:** _none_
- **Types:** 4/4 matched
- **Missing types:** _none_

### 23. ty

- **Target:** `syn.Type`
- **Similarity:** 0.53
- **Dependents:** 1
- **Priority Score:** 1000704.8
- **Functions:** 7/7 matched (target 56)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 24)
- **Missing types:** _none_
- **Lint issues:** 1

### 24. bigint

- **Target:** `syn.BigInt`
- **Similarity:** 0.67
- **Dependents:** 1
- **Priority Score:** 1000603.3
- **Functions:** 5/5 matched (target 8)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 25. derive

- **Target:** `syn.Derive`
- **Similarity:** 0.72
- **Dependents:** 1
- **Priority Score:** 1000502.8
- **Functions:** 5/5 matched (target 15)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 13)
- **Missing types:** _none_

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
- **Similarity:** 0.05
- **Dependents:** 1
- **Priority Score:** 1000109.5
- **Functions:** 1/1 matched (target 29)
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

### 31. export

- **Target:** `syn.Export`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 80800.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/8 matched (target 1)
- **Missing types:** `Formatter`, `FmtResult`, `bool`, `str`, `Span`, `TokenStream2`, `TokenStream`, `private`

### 32. drops

- **Target:** `syn.Drops`
- **Similarity:** 0.45
- **Dependents:** 0
- **Priority Score:** 30905.5
- **Functions:** 4/5 matched (target 9)
- **Missing functions:** `test_needs_drop`
- **Types:** 2/4 matched (target 2)
- **Missing types:** `Target`, `NeedsDrop`
- **Tests:** 0/1 matched

### 33. parse

- **Target:** `syn.Parse`
- **Similarity:** 0.56
- **Dependents:** 0
- **Priority Score:** 24204.4
- **Functions:** 33/33 matched (target 67)
- **Missing functions:** _none_
- **Types:** 7/9 matched (target 17)
- **Missing types:** `Target`, `Output`
- **Lint issues:** 2

### 34. data

- **Target:** `syn.Data`
- **Similarity:** 0.62
- **Dependents:** 0
- **Priority Score:** 21403.8
- **Functions:** 11/11 matched (target 23)
- **Missing functions:** _none_
- **Types:** 1/3 matched (target 12)
- **Missing types:** `Item`, `IntoIter`
- **Lint issues:** 1

### 35. ext

- **Target:** `syn.Ext`
- **Similarity:** 0.82
- **Dependents:** 0
- **Priority Score:** 11401.8
- **Functions:** 7/7 matched (target 12)
- **Missing functions:** _none_
- **Types:** 6/7 matched
- **Missing types:** `Token`

### 36. buffer

- **Target:** `syn.Buffer`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 3502.7
- **Functions:** 31/31 matched (target 33)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 10)
- **Missing types:** _none_

### 37. fixup

- **Target:** `syn.Fixup`
- **Similarity:** 0.65
- **Dependents:** 0
- **Priority Score:** 1703.5
- **Functions:** 15/15 matched (target 24)
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Lint issues:** 1

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
- **Similarity:** 0.74
- **Dependents:** 0
- **Priority Score:** 802.6
- **Functions:** 8/8 matched (target 9)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 40. meta

- **Target:** `syn.Meta`
- **Similarity:** 0.65
- **Dependents:** 0
- **Priority Score:** 603.5
- **Functions:** 5/5 matched (target 7)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 41. thread

- **Target:** `syn.Thread`
- **Similarity:** 0.44
- **Dependents:** 0
- **Priority Score:** 505.6
- **Functions:** 4/4 matched (target 13)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
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

### 45. parse_quote

- **Target:** `syn.ParseQuote`
- **Similarity:** 0.48
- **Dependents:** 0
- **Priority Score:** 205.2
- **Functions:** 1/1 matched (target 8)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 6)
- **Missing types:** _none_

### 46. whitespace

- **Target:** `syn.Whitespace`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 204.9
- **Functions:** 2/2 matched (target 6)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 47. op

- **Target:** `syn.Op`
- **Similarity:** 0.68
- **Dependents:** 0
- **Priority Score:** 203.2
- **Functions:** 2/2 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 35)
- **Missing types:** _none_

### 48. print

- **Target:** `syn.Print`
- **Similarity:** 0.86
- **Dependents:** 0
- **Priority Score:** 201.4
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 49. gen.eq

- **Target:** `gen.Eq`
- **Similarity:** 0.10
- **Dependents:** 0
- **Priority Score:** 109.0
- **Functions:** 1/1 matched (target 20)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 50. gen.hash

- **Target:** `gen.Hash`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 108.5
- **Functions:** 1/1 matched (target 45)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 51. custom_keyword

- **Target:** `syn.CustomKeyword [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 52. parse_macro_input

- **Target:** `syn.ParseMacroInput [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 2)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 3)
- **Missing types:** _none_

### 53. custom_punctuation

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

