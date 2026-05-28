# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 32/97 (33.0%)
- **Function parity:** 163/3214 matched (target 793) — 5.1%
- **Class/type parity:** 33/134 matched (target 332) — 24.6%
- **Combined symbol parity:** 196/3348 matched (target 1125) — 5.9%
- **Average inline-code cosine:** 0.28 (function body across 32 matched files)
- **Average documentation cosine:** 0.38 (doc text across 32 matched files)
- **Cheat-zeroed Files:** 5
- **Critical Issues:** 26 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. punctuated
- **Similarity:** 0.19 (needs 66% improvement)
- **Dependencies:** 19
- **Priority Score:** 19296808.0
- **Functions:** 38/54 matched (target 48)
- **Missing functions:** `clone`, `clone_from`, `eq`, `hash`, `fmt`, `from_iter`, `into_iter`, `default`, `next`, `size_hint`, `next_back`, `clone_box`, `empty_punctuated_iter_mut`, `index`, `index_mut`, `to_tokens`
- **Types:** 1/14 matched (target 3)
- **Missing types:** `Item`, `IntoIter`, `Pairs`, `PairsMut`, `IntoPairs`, `Iter`, `IterTrait`, `PrivateIter`, `IterMut`, `IterMutTrait`, `PrivateIterMut`, `Pair`, `Output`
- **Symbol Deficit:** 29 (functions: 16, types: 13)
- **Action:** Deep review - likely missing major functionality

### 2. token
- **Similarity:** 0.09 (needs 76% improvement)
- **Dependencies:** 18
- **Priority Score:** 18172210.0
- **Functions:** 3/17 matched (target 384)
- **Missing functions:** `peek`, `display`, `parse`, `Group`, `clone`, `fmt`, `eq`, `hash`, `keyword`, `peek_keyword`, `punct`, `punct_helper`, `peek_punct`, `delim`
- **Types:** 2/5 matched (target 110)
- **Missing types:** `Sealed`, `WithSpan`, `CustomToken`
- **Symbol Deficit:** 17 (functions: 14, types: 3)
- **Action:** Deep review - likely missing major functionality

### 3. path
- **Similarity:** 0.29 (needs 56% improvement)
- **Dependencies:** 14
- **Priority Score:** 14182807.0
- **Functions:** 9/26 matched (target 37)
- **Missing functions:** `default`, `const_argument`, `parse_turbofish`, `do_parse`, `parse_helper`, `parse_mod_style`, `parse_rest`, `is_mod_style`, `qpath`, `clone`, `print_path`, `print_path_segment`, `print_path_arguments`, `print_angle_bracketed_generic_arguments`, `print_parenthesized_generic_arguments`, `print_qpath`, `conditionally_print_turbofish`
- **Types:** 1/2 matched (target 24)
- **Missing types:** `QSelfDelimiters`
- **Symbol Deficit:** 18 (functions: 17, types: 1)
- **Action:** Deep review - likely missing major functionality

### 4. ident
- **Similarity:** 0.73 (needs 12% improvement)
- **Dependencies:** 14
- **Priority Score:** 14010603.0
- **Functions:** 5/6 matched (target 14)
- **Missing functions:** `from`
- **Types:** 0/0 matched (target 3)
- **Missing types:** _none_
- **Symbol Deficit:** 1 (functions: 1, types: 0)
- **Action:** Review and complete missing sections

### 5. expr
- **Similarity:** 0.02 (needs 83% improvement)
- **Dependencies:** 12
- **Priority Score:** 12656610.0
- **Functions:** 1/65 matched (target 6)
- **Missing functions:** `parse_without_eager_brace`, `parse_with_earlier_boundary_rule`, `peek`, `replace_attrs`, `from`, `eq`, `hash`, `fmt`, `span`, `is_named`, `parse`, `clone`, `parse_expr`, `parse_binop_rhs`, `peek_precedence`, `ambiguous_expr`, `expr_attrs`, `unary_expr`, `trailer_expr`, `trailer_helper`, `atom_expr`, `atom_labeled`, `expr_builtin`, `path_or_macro_or_struct`, `rest_of_path_or_macro_or_struct`, `paren_or_tuple`, `array_or_repeat`, `continue_parsing_early`, `expr_group`, `expr_let`, `expr_unary`, `expr_become`, `expr_closure`, `closure_arg`, `expr_break`, `expr_struct_helper`, `expr_range`, `parse_range_end`, `parse_obsolete`, `parse_multiple`, `multi_index`, `check_cast`, `outer_attrs_to_tokens`, `inner_attrs_to_tokens`, `print_subexpression`, `print_expr`, `print_expr_assign`, `print_expr_await`, `print_expr_binary`, `print_expr_break`, `print_expr_call`, `print_expr_cast`, `print_expr_closure`, `print_expr_field`, `print_expr_index`, `print_expr_let`, `print_expr_method_call`, `print_expr_range`, `print_expr_raw_addr`, `print_expr_reference`, `print_expr_return`, `print_expr_try`, `print_expr_unary`, `print_expr_yield`
- **Types:** 0/1 matched (target 8)
- **Missing types:** `AllowStruct`
- **Symbol Deficit:** 65 (functions: 64, types: 1)
- **Action:** Deep review - likely missing major functionality

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. punctuated

- **Target:** `syn.Punctuated [PROVENANCE-FALLBACK]`
- **Similarity:** 0.19
- **Dependents:** 19
- **Priority Score:** 19296808.0
- **Functions:** 38/54 matched (target 48)
- **Missing functions:** `clone`, `clone_from`, `eq`, `hash`, `fmt`, `from_iter`, `into_iter`, `default`, `next`, `size_hint`, `next_back`, `clone_box`, `empty_punctuated_iter_mut`, `index`, `index_mut`, `to_tokens`
- **Types:** 1/14 matched (target 3)
- **Missing types:** `Item`, `IntoIter`, `Pairs`, `PairsMut`, `IntoPairs`, `Iter`, `IterTrait`, `PrivateIter`, `IterMut`, `IterMutTrait`, `PrivateIterMut`, `Pair`, `Output`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `punctuated.rs` vs expected `punctuated.rs`
- **Proposed provenance header:** `// port-lint: source punctuated.rs` (current: `// port-lint: source punctuated.rs`)
- **Lint issues:** 1

### 2. token

- **Target:** `token.Token [PROVENANCE-FALLBACK]`
- **Similarity:** 0.09
- **Dependents:** 18
- **Priority Score:** 18172210.0
- **Functions:** 3/17 matched (target 384)
- **Missing functions:** `peek`, `display`, `parse`, `Group`, `clone`, `fmt`, `eq`, `hash`, `keyword`, `peek_keyword`, `punct`, `punct_helper`, `peek_punct`, `delim`
- **Types:** 2/5 matched (target 110)
- **Missing types:** `Sealed`, `WithSpan`, `CustomToken`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `token.rs` vs expected `token.rs`
- **Proposed provenance header:** `// port-lint: source token.rs` (current: `// port-lint: source token.rs`)
- **Lint issues:** 1

### 3. path

- **Target:** `syn.Path [PROVENANCE-FALLBACK]`
- **Similarity:** 0.29
- **Dependents:** 14
- **Priority Score:** 14182807.0
- **Functions:** 9/26 matched (target 37)
- **Missing functions:** `default`, `const_argument`, `parse_turbofish`, `do_parse`, `parse_helper`, `parse_mod_style`, `parse_rest`, `is_mod_style`, `qpath`, `clone`, `print_path`, `print_path_segment`, `print_path_arguments`, `print_angle_bracketed_generic_arguments`, `print_parenthesized_generic_arguments`, `print_qpath`, `conditionally_print_turbofish`
- **Types:** 1/2 matched (target 24)
- **Missing types:** `QSelfDelimiters`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `path.rs` vs expected `path.rs`
- **Proposed provenance header:** `// port-lint: source path.rs` (current: `// port-lint: source path.rs`)
- **Lint issues:** 1

### 4. ident

- **Target:** `syn.Ident [PROVENANCE-FALLBACK]`
- **Similarity:** 0.73
- **Dependents:** 14
- **Priority Score:** 14010603.0
- **Functions:** 5/6 matched (target 14)
- **Missing functions:** `from`
- **Types:** 0/0 matched (target 3)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ident.rs` vs expected `ident.rs`
- **Proposed provenance header:** `// port-lint: source ident.rs` (current: `// port-lint: source ident.rs`)
- **Lint issues:** 1

### 5. expr

- **Target:** `syn.Expr [PROVENANCE-FALLBACK]`
- **Similarity:** 0.02
- **Dependents:** 12
- **Priority Score:** 12656610.0
- **Functions:** 1/65 matched (target 6)
- **Missing functions:** `parse_without_eager_brace`, `parse_with_earlier_boundary_rule`, `peek`, `replace_attrs`, `from`, `eq`, `hash`, `fmt`, `span`, `is_named`, `parse`, `clone`, `parse_expr`, `parse_binop_rhs`, `peek_precedence`, `ambiguous_expr`, `expr_attrs`, `unary_expr`, `trailer_expr`, `trailer_helper`, `atom_expr`, `atom_labeled`, `expr_builtin`, `path_or_macro_or_struct`, `rest_of_path_or_macro_or_struct`, `paren_or_tuple`, `array_or_repeat`, `continue_parsing_early`, `expr_group`, `expr_let`, `expr_unary`, `expr_become`, `expr_closure`, `closure_arg`, `expr_break`, `expr_struct_helper`, `expr_range`, `parse_range_end`, `parse_obsolete`, `parse_multiple`, `multi_index`, `check_cast`, `outer_attrs_to_tokens`, `inner_attrs_to_tokens`, `print_subexpression`, `print_expr`, `print_expr_assign`, `print_expr_await`, `print_expr_binary`, `print_expr_break`, `print_expr_call`, `print_expr_cast`, `print_expr_closure`, `print_expr_field`, `print_expr_index`, `print_expr_let`, `print_expr_method_call`, `print_expr_range`, `print_expr_raw_addr`, `print_expr_reference`, `print_expr_return`, `print_expr_try`, `print_expr_unary`, `print_expr_yield`
- **Types:** 0/1 matched (target 8)
- **Missing types:** `AllowStruct`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `expr.rs` vs expected `expr.rs`
- **Proposed provenance header:** `// port-lint: source expr.rs` (current: `// port-lint: source expr.rs`)
- **Lint issues:** 1

### 6. lifetime

- **Target:** `syn.Lifetime [PROVENANCE-FALLBACK]`
- **Similarity:** 0.39
- **Dependents:** 9
- **Priority Score:** 9061206.0
- **Functions:** 5/11 matched (target 12)
- **Missing functions:** `fmt`, `clone`, `eq`, `partial_cmp`, `cmp`, `hash`
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lifetime.rs` vs expected `lifetime.rs`
- **Proposed provenance header:** `// port-lint: source lifetime.rs` (current: `// port-lint: source lifetime.rs`)
- **Lint issues:** 1

### 7. verbatim

- **Target:** `syn.Verbatim [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 7
- **Priority Score:** 7010110.0
- **Functions:** 0/1 matched
- **Missing functions:** `between`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `verbatim.rs` vs expected `verbatim.rs`
- **Proposed provenance header:** `// port-lint: source verbatim.rs` (current: `// port-lint: source verbatim.rs`)
- **Lint issues:** 1

### 8. lit

- **Target:** `syn.Lit [PROVENANCE-FALLBACK]`
- **Similarity:** 0.13
- **Dependents:** 6
- **Priority Score:** 6344208.5
- **Functions:** 8/38 matched (target 42)
- **Missing functions:** `parse_with`, `respan_token_stream`, `respan_token_tree`, `set_span`, `suffix`, `from`, `fmt`, `debug`, `clone`, `parse_negative_lit`, `peek_impl`, `from_str_for_fuzzing`, `from_str`, `byte`, `next_chr`, `parse_lit_str`, `parse_lit_str_cooked`, `parse_lit_str_raw`, `parse_lit_byte_str`, `parse_lit_byte_str_cooked`, `parse_lit_byte_str_raw`, `parse_lit_c_str`, `parse_lit_c_str_cooked`, `parse_lit_c_str_raw`, `parse_lit_byte`, `parse_lit_char`, `backslash_x`, `backslash_u`, `parse_lit_int`, `parse_lit_float`
- **Types:** 0/4 matched (target 22)
- **Missing types:** `LitRepr`, `LitIntRepr`, `LitFloatRepr`, `StrStyle`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lit.rs` vs expected `lit.rs`
- **Proposed provenance header:** `// port-lint: source lit.rs` (current: `// port-lint: source lit.rs`)
- **Lint issues:** 1

### 9. lookahead

- **Target:** `syn.Lookahead [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 5
- **Priority Score:** 5061410.0
- **Functions:** 4/8 matched (target 6)
- **Missing functions:** `new`, `peek_impl`, `fmt`, `clone`
- **Types:** 4/6 matched (target 4)
- **Missing types:** `CommaSeparated`, `Token`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lookahead.rs` vs expected `lookahead.rs`
- **Proposed provenance header:** `// port-lint: source lookahead.rs` (current: `// port-lint: source lookahead.rs`)
- **Lint issues:** 1

### 10. span

- **Target:** `syn.Span [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 4
- **Priority Score:** 4010210.0
- **Functions:** 0/1 matched (target 9)
- **Missing functions:** `into_spans`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `span.rs` vs expected `span.rs`
- **Proposed provenance header:** `// port-lint: source span.rs` (current: `// port-lint: source span.rs`)
- **Lint issues:** 1

### 11. generics

- **Target:** `syn.Generics [PROVENANCE-FALLBACK]`
- **Similarity:** 0.21
- **Dependents:** 3
- **Priority Score:** 3192508.0
- **Functions:** 5/15 matched (target 26)
- **Missing functions:** `next`, `as_turbofish`, `from`, `parse`, `parse_single`, `parse_multiple`, `do_parse`, `choose_generics_over_qpath`, `choose_generics_over_qpath_after_keyword`, `print_const_argument`
- **Types:** 1/10 matched (target 14)
- **Missing types:** `Lifetimes`, `Item`, `LifetimesMut`, `TypeParams`, `TypeParamsMut`, `ConstParams`, `ConstParamsMut`, `ImplGenerics`, `TypeGenerics`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `generics.rs` vs expected `generics.rs`
- **Proposed provenance header:** `// port-lint: source generics.rs` (current: `// port-lint: source generics.rs`)
- **Lint issues:** 1

### 12. error

- **Target:** `syn.Error [PROVENANCE-FALLBACK]`
- **Similarity:** 0.37
- **Dependents:** 3
- **Priority Score:** 3112206.2
- **Functions:** 9/14 matched (target 25)
- **Missing functions:** `new_at`, `fmt`, `clone`, `into_iter`, `next`
- **Types:** 2/8 matched (target 6)
- **Missing types:** `Result`, `Error`, `_Test`, `Item`, `IntoIter`, `Iter`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `error.rs` vs expected `error.rs`
- **Proposed provenance header:** `// port-lint: source error.rs` (current: `// port-lint: source error.rs`)
- **Lint issues:** 1

### 13. ty

- **Target:** `syn.Type [PROVENANCE-FALLBACK]`
- **Similarity:** 0.31
- **Dependents:** 2
- **Priority Score:** 2060706.9
- **Functions:** 1/7 matched (target 41)
- **Missing functions:** `parse`, `without_plus`, `ambig_ty`, `parse_bounds`, `parse_bare_fn_arg`, `parse_bare_variadic`
- **Types:** 0/0 matched (target 23)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ty.rs` vs expected `ty.rs`
- **Proposed provenance header:** `// port-lint: source ty.rs` (current: `// port-lint: source ty.rs`)
- **Lint issues:** 2

### 14. precedence

- **Target:** `syn.Precedence [PROVENANCE-FALLBACK]`
- **Similarity:** 0.13
- **Dependents:** 2
- **Priority Score:** 2050708.6
- **Functions:** 1/6 matched (target 1)
- **Missing functions:** `of`, `prefix_attrs`, `clone`, `eq`, `partial_cmp`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `precedence.rs` vs expected `precedence.rs`
- **Proposed provenance header:** `// port-lint: source precedence.rs` (current: `// port-lint: source precedence.rs`)
- **Lint issues:** 1

### 15. spanned

- **Target:** `syn.Spanned [PROVENANCE-FALLBACK]`
- **Similarity:** 0.97
- **Dependents:** 2
- **Priority Score:** 2010300.2
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Sealed`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `spanned.rs` vs expected `spanned.rs`
- **Proposed provenance header:** `// port-lint: source spanned.rs` (current: `// port-lint: source spanned.rs`)
- **Lint issues:** 1

### 16. attr

- **Target:** `syn.Attr [PROVENANCE-FALLBACK]`
- **Similarity:** 0.08
- **Dependents:** 1
- **Priority Score:** 1252709.2
- **Functions:** 2/23 matched (target 11)
- **Missing functions:** `parse_args`, `parse_args_with`, `parse_outer`, `parse_inner`, `require_path_only`, `require_list`, `require_name_value`, `outer`, `is_outer`, `inner`, `is_inner`, `from`, `single_parse_inner`, `single_parse_outer`, `parse`, `parse_outermost_meta_path`, `parse_meta_after_path`, `parse_meta_list_after_path`, `parse_meta_name_value_after_path`, `fmt`, `to_tokens`
- **Types:** 0/4 matched (target 11)
- **Missing types:** `FilterAttrs`, `Ret`, `DisplayAttrStyle`, `DisplayPath`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `attr.rs` vs expected `attr.rs`
- **Proposed provenance header:** `// port-lint: source attr.rs` (current: `// port-lint: source attr.rs`)
- **Lint issues:** 1

### 17. parse

- **Target:** `syn.Parse [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1104210.0
- **Functions:** 25/33 matched (target 54)
- **Missing functions:** `drop`, `fmt`, `deref`, `default`, `cell_clone`, `to_tokens`, `eq`, `hash`
- **Types:** 7/9 matched (target 16)
- **Missing types:** `Target`, `Output`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `parse.rs` vs expected `parse.rs`
- **Proposed provenance header:** `// port-lint: source parse.rs` (current: `// port-lint: source parse.rs`)
- **Lint issues:** 2

### 18. bigint

- **Target:** `syn.BigInt [PROVENANCE-FALLBACK]`
- **Similarity:** 0.32
- **Dependents:** 1
- **Priority Score:** 1030606.8
- **Functions:** 2/5 matched
- **Missing functions:** `new`, `add_assign`, `mul_assign`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `bigint.rs` vs expected `bigint.rs`
- **Proposed provenance header:** `// port-lint: source bigint.rs` (current: `// port-lint: source bigint.rs`)
- **Lint issues:** 1

### 19. group

- **Target:** `syn.Group [PROVENANCE-FALLBACK]`
- **Similarity:** 0.88
- **Dependents:** 1
- **Priority Score:** 1010901.2
- **Functions:** 5/5 matched (target 8)
- **Missing functions:** _none_
- **Types:** 3/4 matched
- **Missing types:** `Group`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `group.rs` vs expected `group.rs`
- **Proposed provenance header:** `// port-lint: source group.rs` (current: `// port-lint: source group.rs`)
- **Lint issues:** 1

### 20. sealed

- **Target:** `syn.Sealed [PROVENANCE-FALLBACK]`
- **Similarity:** 1.00
- **Dependents:** 1
- **Priority Score:** 1000100.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `sealed.rs` vs expected `sealed.rs`
- **Proposed provenance header:** `// port-lint: source sealed.rs` (current: `// port-lint: source sealed.rs`)
- **Lint issues:** 1

### 21. data

- **Target:** `syn.Data [PROVENANCE-FALLBACK]`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 121409.4
- **Functions:** 2/11 matched (target 5)
- **Missing functions:** `iter`, `iter_mut`, `into_iter`, `next`, `clone`, `parse`, `parse_named`, `parse_unnamed`, `to_tokens`
- **Types:** 0/3 matched (target 16)
- **Missing types:** `Item`, `IntoIter`, `Members`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `data.rs` vs expected `data.rs`
- **Proposed provenance header:** `// port-lint: source data.rs` (current: `// port-lint: source data.rs`)
- **Lint issues:** 1

### 22. ext

- **Target:** `syn.Ext [PROVENANCE-FALLBACK]`
- **Similarity:** 0.39
- **Dependents:** 0
- **Priority Score:** 111406.1
- **Functions:** 3/7 matched (target 6)
- **Missing functions:** `parse_any`, `append`, `new_spanned`, `clone`
- **Types:** 0/7 matched (target 1)
- **Missing types:** `IdentExt`, `Token`, `TokenStreamExt`, `PunctExt`, `Sealed`, `PeekFn`, `IdentAny`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ext.rs` vs expected `ext.rs`
- **Proposed provenance header:** `// port-lint: source ext.rs` (current: `// port-lint: source ext.rs`)
- **Lint issues:** 1

### 23. buffer

- **Target:** `syn.Buffer [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 53510.0
- **Functions:** 27/31 matched (target 29)
- **Missing functions:** `new`, `clone`, `eq`, `partial_cmp`
- **Types:** 3/4 matched (target 9)
- **Missing types:** `UnsafeSyncEntry`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `buffer.rs` vs expected `buffer.rs`
- **Proposed provenance header:** `// port-lint: source buffer.rs` (current: `// port-lint: source buffer.rs`)
- **Lint issues:** 1

### 24. tt

- **Target:** `syn.Tt [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 40410.0
- **Functions:** 0/2 matched (target 5)
- **Missing functions:** `eq`, `hash`
- **Types:** 0/2 matched (target 0)
- **Missing types:** `TokenTreeHelper`, `TokenStreamHelper`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tt.rs` vs expected `tt.rs`
- **Proposed provenance header:** `// port-lint: source tt.rs` (current: `// port-lint: source tt.rs`)
- **Lint issues:** 1

### 25. restriction

- **Target:** `syn.Restriction [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 40410.0
- **Functions:** 0/4 matched (target 0)
- **Missing functions:** `parse`, `parse_pub`, `is_some`, `to_tokens`
- **Types:** 0/0 matched (target 8)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `restriction.rs` vs expected `restriction.rs`
- **Proposed provenance header:** `// port-lint: source restriction.rs` (current: `// port-lint: source restriction.rs`)
- **Lint issues:** 1

### 26. thread

- **Target:** `syn.Thread [PROVENANCE-FALLBACK]`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 20507.0
- **Functions:** 2/4 matched (target 3)
- **Missing functions:** `fmt`, `clone`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `thread.rs` vs expected `thread.rs`
- **Proposed provenance header:** `// port-lint: source thread.rs` (current: `// port-lint: source thread.rs`)
- **Lint issues:** 1

### 27. discouraged

- **Target:** `syn.Discouraged [PROVENANCE-FALLBACK]`
- **Similarity:** 0.71
- **Dependents:** 0
- **Priority Score:** 20402.9
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 0/2 matched (target 1)
- **Missing types:** `Speculative`, `AnyDelimiter`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `discouraged.rs` vs expected `discouraged.rs`
- **Proposed provenance header:** `// port-lint: source discouraged.rs` (current: `// port-lint: source discouraged.rs`)
- **Lint issues:** 1

### 28. whitespace

- **Target:** `syn.Whitespace [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 20210.0
- **Functions:** 0/2 matched
- **Missing functions:** `skip`, `is_whitespace`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `whitespace.rs` vs expected `whitespace.rs`
- **Proposed provenance header:** `// port-lint: source whitespace.rs` (current: `// port-lint: source whitespace.rs`)
- **Lint issues:** 1

### 29. op

- **Target:** `syn.Op [PROVENANCE-FALLBACK]`
- **Similarity:** 0.37
- **Dependents:** 0
- **Priority Score:** 10206.3
- **Functions:** 1/2 matched
- **Missing functions:** `parse`
- **Types:** 0/0 matched (target 33)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `op.rs` vs expected `op.rs`
- **Proposed provenance header:** `// port-lint: source op.rs` (current: `// port-lint: source op.rs`)
- **Lint issues:** 1

### 30. parse_quote

- **Target:** `syn.ParseQuote [PROVENANCE-FALLBACK]`
- **Similarity:** 0.09
- **Dependents:** 0
- **Priority Score:** 209.1
- **Functions:** 1/1 matched (target 3)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `parse_quote.rs` vs expected `parse_quote.rs`
- **Proposed provenance header:** `// port-lint: source parse_quote.rs` (current: `// port-lint: source parse_quote.rs`)
- **Lint issues:** 1

### 31. print

- **Target:** `syn.Print [PROVENANCE-FALLBACK]`
- **Similarity:** 0.86
- **Dependents:** 0
- **Priority Score:** 201.4
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `print.rs` vs expected `print.rs`
- **Proposed provenance header:** `// port-lint: source print.rs` (current: `// port-lint: source print.rs`)
- **Lint issues:** 1

### 32. parse_macro_input

- **Target:** `syn.ParseMacroInput [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched (target 2)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 3)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `parse_macro_input.rs` vs expected `parse_macro_input.rs`
- **Proposed provenance header:** `// port-lint: source parse_macro_input.rs` (current: `// port-lint: source parse_macro_input.rs`)
- **Lint issues:** 1

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
| `lib` | `Lib` | 0 | `src/lib.rs` | `Lib.kt` |
| `common.mod` | `tests.common.Mod` | 0 | `tests/common/mod.rs` | `tests/common/Mod.kt` |
| `debug.mod` | `tests.debug.Mod` | 0 | `tests/debug/mod.rs` | `tests/debug/Mod.kt` |
| `macros.mod` | `tests.macros.Mod` | 0 | `tests/macros/mod.rs` | `tests/macros/Mod.kt` |
| `repo.mod` | `tests.repo.Mod` | 0 | `tests/repo/mod.rs` | `tests/repo/Mod.kt` |
| `snapshot.mod` | `tests.snapshot.Mod` | 0 | `tests/snapshot/mod.rs` | `tests/snapshot/Mod.kt` |

