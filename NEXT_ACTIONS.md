# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 32/55 (58.2%)
- **Function parity:** 126/2541 matched (target 625) — 5.0%
- **Class/type parity:** 33/121 matched (target 313) — 27.3%
- **Combined symbol parity:** 159/2662 matched (target 938) — 6.0%
- **Average inline-code cosine:** 0.17 (function body across 32 matched files)
- **Average documentation cosine:** 0.38 (doc text across 32 matched files)
- **Cheat-zeroed Files:** 12
- **Critical Issues:** 29 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. token
- **Similarity:** 0.09 (needs 76% improvement)
- **Dependencies:** 17
- **Priority Score:** 17172210.0
- **Functions:** 3/17 matched (target 383)
- **Missing functions:** `peek`, `display`, `parse`, `Group`, `clone`, `fmt`, `eq`, `hash`, `keyword`, `peek_keyword`, `punct`, `punct_helper`, `peek_punct`, `delim`
- **Types:** 2/5 matched (target 110)
- **Missing types:** `Sealed`, `WithSpan`, `CustomToken`
- **Symbol Deficit:** 17 (functions: 14, types: 3)
- **Action:** Deep review - likely missing major functionality

### 2. ident
- **Similarity:** 0.13 (needs 72% improvement)
- **Dependencies:** 14
- **Priority Score:** 14050609.0
- **Functions:** 1/6 matched (target 4)
- **Missing functions:** `from`, `accept_as_ident`, `parse`, `peek`, `display`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Symbol Deficit:** 5 (functions: 5, types: 0)
- **Action:** Deep review - likely missing major functionality

### 3. punctuated
- **Similarity:** 0.14 (needs 71% improvement)
- **Dependencies:** 13
- **Priority Score:** 13466809.0
- **Functions:** 21/54 matched (target 26)
- **Missing functions:** `first_mut`, `last_mut`, `get_mut`, `iter`, `iter_mut`, `pairs_mut`, `into_pairs`, `clone`, `clone_from`, `eq`, `hash`, `fmt`, `from_iter`, `extend`, `do_extend`, `into_iter`, `default`, `next`, `size_hint`, `next_back`, `clone_box`, `empty_punctuated_iter_mut`, `into_value`, `value`, `value_mut`, `punct`, `punct_mut`, `into_tuple`, `cloned`, `index`, `index_mut`, `fold`, `to_tokens`
- **Types:** 1/14 matched (target 3)
- **Missing types:** `Item`, `IntoIter`, `Pairs`, `PairsMut`, `IntoPairs`, `Iter`, `IterTrait`, `PrivateIter`, `IterMut`, `IterMutTrait`, `PrivateIterMut`, `Pair`, `Output`
- **Symbol Deficit:** 46 (functions: 33, types: 13)
- **Action:** Deep review - likely missing major functionality

### 4. expr
- **Similarity:** 0.00 (needs 85% improvement)
- **Dependencies:** 10
- **Priority Score:** 10666610.0
- **Functions:** 0/65 matched (target 1)
- **Missing functions:** `parse_without_eager_brace`, `parse_with_earlier_boundary_rule`, `peek`, `replace_attrs`, `from`, `eq`, `hash`, `fmt`, `span`, `is_named`, `parse`, `clone`, `parse_expr`, `parse_binop_rhs`, `peek_precedence`, `ambiguous_expr`, `expr_attrs`, `unary_expr`, `trailer_expr`, `trailer_helper`, `atom_expr`, `atom_labeled`, `expr_builtin`, `path_or_macro_or_struct`, `rest_of_path_or_macro_or_struct`, `paren_or_tuple`, `array_or_repeat`, `continue_parsing_early`, `expr_group`, `expr_let`, `expr_unary`, `expr_become`, `expr_closure`, `closure_arg`, `expr_break`, `expr_struct_helper`, `expr_range`, `parse_range_end`, `parse_obsolete`, `parse_multiple`, `multi_index`, `check_cast`, `outer_attrs_to_tokens`, `inner_attrs_to_tokens`, `print_subexpression`, `print_expr`, `to_tokens`, `print_expr_assign`, `print_expr_await`, `print_expr_binary`, `print_expr_break`, `print_expr_call`, `print_expr_cast`, `print_expr_closure`, `print_expr_field`, `print_expr_index`, `print_expr_let`, `print_expr_method_call`, `print_expr_range`, `print_expr_raw_addr`, `print_expr_reference`, `print_expr_return`, `print_expr_try`, `print_expr_unary`, `print_expr_yield`
- **Types:** 0/1 matched (target 8)
- **Missing types:** `AllowStruct`
- **Symbol Deficit:** 66 (functions: 65, types: 1)
- **Action:** Deep review - likely missing major functionality

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. token

- **Target:** `token.Token`
- **Similarity:** 0.09
- **Dependents:** 17
- **Priority Score:** 17172210.0
- **Functions:** 3/17 matched (target 383)
- **Missing functions:** `peek`, `display`, `parse`, `Group`, `clone`, `fmt`, `eq`, `hash`, `keyword`, `peek_keyword`, `punct`, `punct_helper`, `peek_punct`, `delim`
- **Types:** 2/5 matched (target 110)
- **Missing types:** `Sealed`, `WithSpan`, `CustomToken`

### 2. ident

- **Target:** `syn.Ident`
- **Similarity:** 0.13
- **Dependents:** 14
- **Priority Score:** 14050609.0
- **Functions:** 1/6 matched (target 4)
- **Missing functions:** `from`, `accept_as_ident`, `parse`, `peek`, `display`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 3. punctuated

- **Target:** `syn.Punctuated`
- **Similarity:** 0.14
- **Dependents:** 13
- **Priority Score:** 13466809.0
- **Functions:** 21/54 matched (target 26)
- **Missing functions:** `first_mut`, `last_mut`, `get_mut`, `iter`, `iter_mut`, `pairs_mut`, `into_pairs`, `clone`, `clone_from`, `eq`, `hash`, `fmt`, `from_iter`, `extend`, `do_extend`, `into_iter`, `default`, `next`, `size_hint`, `next_back`, `clone_box`, `empty_punctuated_iter_mut`, `into_value`, `value`, `value_mut`, `punct`, `punct_mut`, `into_tuple`, `cloned`, `index`, `index_mut`, `fold`, `to_tokens`
- **Types:** 1/14 matched (target 3)
- **Missing types:** `Item`, `IntoIter`, `Pairs`, `PairsMut`, `IntoPairs`, `Iter`, `IterTrait`, `PrivateIter`, `IterMut`, `IterMutTrait`, `PrivateIterMut`, `Pair`, `Output`

### 4. expr

- **Target:** `syn.Expr`
- **Similarity:** 0.00
- **Dependents:** 10
- **Priority Score:** 10666610.0
- **Functions:** 0/65 matched (target 1)
- **Missing functions:** `parse_without_eager_brace`, `parse_with_earlier_boundary_rule`, `peek`, `replace_attrs`, `from`, `eq`, `hash`, `fmt`, `span`, `is_named`, `parse`, `clone`, `parse_expr`, `parse_binop_rhs`, `peek_precedence`, `ambiguous_expr`, `expr_attrs`, `unary_expr`, `trailer_expr`, `trailer_helper`, `atom_expr`, `atom_labeled`, `expr_builtin`, `path_or_macro_or_struct`, `rest_of_path_or_macro_or_struct`, `paren_or_tuple`, `array_or_repeat`, `continue_parsing_early`, `expr_group`, `expr_let`, `expr_unary`, `expr_become`, `expr_closure`, `closure_arg`, `expr_break`, `expr_struct_helper`, `expr_range`, `parse_range_end`, `parse_obsolete`, `parse_multiple`, `multi_index`, `check_cast`, `outer_attrs_to_tokens`, `inner_attrs_to_tokens`, `print_subexpression`, `print_expr`, `to_tokens`, `print_expr_assign`, `print_expr_await`, `print_expr_binary`, `print_expr_break`, `print_expr_call`, `print_expr_cast`, `print_expr_closure`, `print_expr_field`, `print_expr_index`, `print_expr_let`, `print_expr_method_call`, `print_expr_range`, `print_expr_raw_addr`, `print_expr_reference`, `print_expr_return`, `print_expr_try`, `print_expr_unary`, `print_expr_yield`
- **Types:** 0/1 matched (target 8)
- **Missing types:** `AllowStruct`

### 5. path

- **Target:** `syn.Path`
- **Similarity:** 0.12
- **Dependents:** 9
- **Priority Score:** 9222809.0
- **Functions:** 6/26 matched (target 13)
- **Missing functions:** `default`, `parse`, `const_argument`, `parse_turbofish`, `do_parse`, `parse_helper`, `parse_mod_style`, `parse_rest`, `is_mod_style`, `qpath`, `clone`, `to_tokens`, `print_path`, `print_path_segment`, `print_path_arguments`, `print_angle_bracketed_generic_arguments`, `print_parenthesized_generic_arguments`, `print_qpath`, `conditionally_print_turbofish`, `span`
- **Types:** 0/2 matched (target 17)
- **Missing types:** `PathStyle`, `QSelfDelimiters`

### 6. lifetime

- **Target:** `syn.Lifetime`
- **Similarity:** 0.33
- **Dependents:** 8
- **Priority Score:** 8071206.5
- **Functions:** 4/11 matched (target 9)
- **Missing functions:** `fmt`, `clone`, `eq`, `partial_cmp`, `cmp`, `hash`, `parse`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 7. verbatim

- **Target:** `syn.Verbatim`
- **Similarity:** 0.00
- **Dependents:** 7
- **Priority Score:** 7010110.0
- **Functions:** 0/1 matched
- **Missing functions:** `between`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 8. lit

- **Target:** `syn.Lit`
- **Similarity:** 0.02
- **Dependents:** 5
- **Priority Score:** 5384210.0
- **Functions:** 4/38 matched (target 5)
- **Missing functions:** `parse`, `parse_with`, `respan_token_stream`, `respan_token_tree`, `set_span`, `suffix`, `token`, `base10_digits`, `base10_parse`, `from`, `fmt`, `debug`, `clone`, `parse_negative_lit`, `peek_impl`, `from_str_for_fuzzing`, `from_str`, `byte`, `next_chr`, `parse_lit_str`, `parse_lit_str_cooked`, `parse_lit_str_raw`, `parse_lit_byte_str`, `parse_lit_byte_str_cooked`, `parse_lit_byte_str_raw`, `parse_lit_c_str`, `parse_lit_c_str_cooked`, `parse_lit_c_str_raw`, `parse_lit_byte`, `parse_lit_char`, `backslash_x`, `backslash_u`, `parse_lit_int`, `parse_lit_float`
- **Types:** 0/4 matched (target 16)
- **Missing types:** `LitRepr`, `LitIntRepr`, `LitFloatRepr`, `StrStyle`

### 9. lookahead

- **Target:** `syn.Lookahead [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 5
- **Priority Score:** 5061410.0
- **Functions:** 4/8 matched (target 6)
- **Missing functions:** `new`, `peek_impl`, `fmt`, `clone`
- **Types:** 4/6 matched (target 4)
- **Missing types:** `CommaSeparated`, `Token`

### 10. span

- **Target:** `syn.Span`
- **Similarity:** 0.00
- **Dependents:** 4
- **Priority Score:** 4010210.0
- **Functions:** 0/1 matched (target 9)
- **Missing functions:** `into_spans`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 11. error

- **Target:** `syn.Error`
- **Similarity:** 0.35
- **Dependents:** 3
- **Priority Score:** 3102206.5
- **Functions:** 8/14 matched (target 22)
- **Missing functions:** `new_at`, `fmt`, `clone`, `into_iter`, `next`, `extend`
- **Types:** 4/8 matched (target 6)
- **Missing types:** `_Test`, `Item`, `IntoIter`, `Iter`

### 12. generics

- **Target:** `syn.Generics [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2252510.0
- **Functions:** 0/15 matched (target 0)
- **Missing functions:** `default`, `make_where_clause`, `split_for_impl`, `next`, `as_turbofish`, `new`, `from`, `parse`, `parse_single`, `parse_multiple`, `do_parse`, `choose_generics_over_qpath`, `choose_generics_over_qpath_after_keyword`, `to_tokens`, `print_const_argument`
- **Types:** 0/10 matched (target 12)
- **Missing types:** `Lifetimes`, `Item`, `LifetimesMut`, `TypeParams`, `TypeParamsMut`, `ConstParams`, `ConstParamsMut`, `ImplGenerics`, `TypeGenerics`, `Turbofish`

### 13. precedence

- **Target:** `syn.Precedence`
- **Similarity:** 0.13
- **Dependents:** 2
- **Priority Score:** 2050708.6
- **Functions:** 1/6 matched (target 1)
- **Missing functions:** `of`, `prefix_attrs`, `clone`, `eq`, `partial_cmp`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 14. attr

- **Target:** `syn.Attr`
- **Similarity:** 0.08
- **Dependents:** 1
- **Priority Score:** 1252709.1
- **Functions:** 2/23 matched (target 11)
- **Missing functions:** `parse_args`, `parse_args_with`, `parse_outer`, `parse_inner`, `require_path_only`, `require_list`, `require_name_value`, `outer`, `is_outer`, `inner`, `is_inner`, `from`, `single_parse_inner`, `single_parse_outer`, `parse`, `parse_outermost_meta_path`, `parse_meta_after_path`, `parse_meta_list_after_path`, `parse_meta_name_value_after_path`, `fmt`, `to_tokens`
- **Types:** 0/4 matched (target 11)
- **Missing types:** `FilterAttrs`, `Ret`, `DisplayAttrStyle`, `DisplayPath`

### 15. ty

- **Target:** `syn.Type`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1070710.0
- **Functions:** 0/7 matched (target 4)
- **Missing functions:** `parse`, `without_plus`, `ambig_ty`, `parse_bounds`, `parse_bare_fn_arg`, `parse_bare_variadic`, `to_tokens`
- **Types:** 0/0 matched (target 23)
- **Missing types:** _none_

### 16. bigint

- **Target:** `syn.BigInt`
- **Similarity:** 0.32
- **Dependents:** 1
- **Priority Score:** 1030606.8
- **Functions:** 2/5 matched
- **Missing functions:** `new`, `add_assign`, `mul_assign`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 17. group

- **Target:** `syn.Group [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1010910.0
- **Functions:** 5/5 matched (target 8)
- **Missing functions:** _none_
- **Types:** 3/4 matched
- **Missing types:** `Group`

### 18. spanned

- **Target:** `syn.Spanned`
- **Similarity:** 0.97
- **Dependents:** 1
- **Priority Score:** 1010300.3
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Sealed`

### 19. sealed

- **Target:** `syn.Sealed`
- **Similarity:** 1.00
- **Dependents:** 1
- **Priority Score:** 1000100.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 20. data

- **Target:** `syn.Data`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 121409.4
- **Functions:** 2/11 matched (target 5)
- **Missing functions:** `iter`, `iter_mut`, `into_iter`, `next`, `clone`, `parse`, `parse_named`, `parse_unnamed`, `to_tokens`
- **Types:** 0/3 matched (target 16)
- **Missing types:** `Item`, `IntoIter`, `Members`

### 21. ext

- **Target:** `syn.Ext [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 111410.0
- **Functions:** 3/7 matched (target 6)
- **Missing functions:** `parse_any`, `append`, `new_spanned`, `clone`
- **Types:** 0/7 matched (target 1)
- **Missing types:** `IdentExt`, `Token`, `TokenStreamExt`, `PunctExt`, `Sealed`, `PeekFn`, `IdentAny`

### 22. parse

- **Target:** `syn.Parse [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 104210.0
- **Functions:** 25/33 matched (target 54)
- **Missing functions:** `drop`, `fmt`, `deref`, `default`, `cell_clone`, `to_tokens`, `eq`, `hash`
- **Types:** 7/9 matched (target 16)
- **Missing types:** `Target`, `Output`
- **Lint issues:** 1

### 23. buffer

- **Target:** `syn.Buffer [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 53510.0
- **Functions:** 27/31 matched (target 29)
- **Missing functions:** `new`, `clone`, `eq`, `partial_cmp`
- **Types:** 3/4 matched (target 9)
- **Missing types:** `UnsafeSyncEntry`

### 24. tt

- **Target:** `syn.Tt [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 40410.0
- **Functions:** 0/2 matched (target 5)
- **Missing functions:** `eq`, `hash`
- **Types:** 0/2 matched (target 0)
- **Missing types:** `TokenTreeHelper`, `TokenStreamHelper`

### 25. restriction

- **Target:** `syn.Restriction [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 40410.0
- **Functions:** 0/4 matched (target 0)
- **Missing functions:** `parse`, `parse_pub`, `is_some`, `to_tokens`
- **Types:** 0/0 matched (target 8)
- **Missing types:** _none_

### 26. thread

- **Target:** `syn.Thread`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 20507.0
- **Functions:** 2/4 matched (target 3)
- **Missing functions:** `fmt`, `clone`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 27. discouraged

- **Target:** `syn.Discouraged [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 20410.0
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 0/2 matched (target 1)
- **Missing types:** `Speculative`, `AnyDelimiter`

### 28. whitespace

- **Target:** `syn.Whitespace [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 20210.0
- **Functions:** 0/2 matched
- **Missing functions:** `skip`, `is_whitespace`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 29. op

- **Target:** `syn.Op`
- **Similarity:** 0.37
- **Dependents:** 0
- **Priority Score:** 10206.3
- **Functions:** 1/2 matched
- **Missing functions:** `parse`
- **Types:** 0/0 matched (target 33)
- **Missing types:** _none_

### 30. parse_quote

- **Target:** `syn.ParseQuote [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 210.0
- **Functions:** 1/1 matched (target 3)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 31. print

- **Target:** `syn.Print`
- **Similarity:** 0.86
- **Dependents:** 0
- **Priority Score:** 201.4
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 32. parse_macro_input

- **Target:** `syn.ParseMacroInput [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 2)
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

