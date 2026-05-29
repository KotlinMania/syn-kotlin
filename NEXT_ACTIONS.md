# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 48/97 (49.5%)
- **Function parity:** 194/1757 matched (target 1310) — 11.0%
- **Class/type parity:** 38/134 matched (target 623) — 28.4%
- **Combined symbol parity:** 232/1891 matched (target 1933) — 12.3%
- **Average inline-code cosine:** 0.29 (function body across 43 matched files)
- **Average documentation cosine:** 0.35 (doc text across 43 matched files)
- **Cheat-zeroed Files:** 13
- **Critical Issues:** 42 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. punctuated
- **Similarity:** 0.22 (needs 63% improvement)
- **Dependencies:** 19
- **Priority Score:** 19326808.0
- **Functions:** 34/54 matched (target 56)
- **Missing functions:** `first_mut`, `last_mut`, `get_mut`, `iter_mut`, `clone`, `clone_from`, `eq`, `hash`, `fmt`, `from_iter`, `do_extend`, `default`, `size_hint`, `next_back`, `clone_box`, `value_mut`, `punct_mut`, `cloned`, `index`, `index_mut`
- **Types:** 2/14 matched (target 5)
- **Missing types:** `Item`, `Pairs`, `PairsMut`, `IntoPairs`, `Iter`, `IterTrait`, `PrivateIter`, `IterMut`, `IterMutTrait`, `PrivateIterMut`, `Pair`, `Output`
- **Symbol Deficit:** 32 (functions: 20, types: 12)
- **Action:** Deep review - likely missing major functionality

### 2. token
- **Similarity:** 0.00 (needs 85% improvement)
- **Dependencies:** 18
- **Priority Score:** 18142210.0
- **Functions:** 6/17 matched (target 679)
- **Missing functions:** `Group`, `clone`, `fmt`, `eq`, `hash`, `keyword`, `peek_keyword`, `punct`, `punct_helper`, `peek_punct`, `delim`
- **Types:** 2/5 matched (target 307)
- **Missing types:** `Sealed`, `WithSpan`, `CustomToken`
- **Symbol Deficit:** 14 (functions: 11, types: 3)
- **Action:** Deep review - likely missing major functionality

### 3. path
- **Similarity:** 0.32 (needs 53% improvement)
- **Dependencies:** 14
- **Priority Score:** 14182807.0
- **Functions:** 9/26 matched (target 39)
- **Missing functions:** `default`, `const_argument`, `parse_turbofish`, `do_parse`, `parse_helper`, `parse_mod_style`, `parse_rest`, `is_mod_style`, `qpath`, `clone`, `print_path`, `print_path_segment`, `print_path_arguments`, `print_angle_bracketed_generic_arguments`, `print_parenthesized_generic_arguments`, `print_qpath`, `conditionally_print_turbofish`
- **Types:** 1/2 matched (target 25)
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
- **Similarity:** 0.00 (needs 85% improvement)
- **Dependencies:** 12
- **Priority Score:** 12656610.0
- **Functions:** 1/65 matched (target 91)
- **Missing functions:** `parse_without_eager_brace`, `parse_with_earlier_boundary_rule`, `peek`, `replace_attrs`, `from`, `eq`, `hash`, `fmt`, `span`, `is_named`, `parse`, `clone`, `parse_expr`, `parse_binop_rhs`, `peek_precedence`, `ambiguous_expr`, `expr_attrs`, `unary_expr`, `trailer_expr`, `trailer_helper`, `atom_expr`, `atom_labeled`, `expr_builtin`, `path_or_macro_or_struct`, `rest_of_path_or_macro_or_struct`, `paren_or_tuple`, `array_or_repeat`, `continue_parsing_early`, `expr_group`, `expr_let`, `expr_unary`, `expr_become`, `expr_closure`, `closure_arg`, `expr_break`, `expr_struct_helper`, `expr_range`, `parse_range_end`, `parse_obsolete`, `parse_multiple`, `multi_index`, `check_cast`, `outer_attrs_to_tokens`, `inner_attrs_to_tokens`, `print_subexpression`, `print_expr`, `print_expr_assign`, `print_expr_await`, `print_expr_binary`, `print_expr_break`, `print_expr_call`, `print_expr_cast`, `print_expr_closure`, `print_expr_field`, `print_expr_index`, `print_expr_let`, `print_expr_method_call`, `print_expr_range`, `print_expr_raw_addr`, `print_expr_reference`, `print_expr_return`, `print_expr_try`, `print_expr_unary`, `print_expr_yield`
- **Types:** 0/1 matched (target 51)
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
- **Similarity:** 0.22
- **Dependents:** 19
- **Priority Score:** 19326808.0
- **Functions:** 34/54 matched (target 56)
- **Missing functions:** `first_mut`, `last_mut`, `get_mut`, `iter_mut`, `clone`, `clone_from`, `eq`, `hash`, `fmt`, `from_iter`, `do_extend`, `default`, `size_hint`, `next_back`, `clone_box`, `value_mut`, `punct_mut`, `cloned`, `index`, `index_mut`
- **Types:** 2/14 matched (target 5)
- **Missing types:** `Item`, `Pairs`, `PairsMut`, `IntoPairs`, `Iter`, `IterTrait`, `PrivateIter`, `IterMut`, `IterMutTrait`, `PrivateIterMut`, `Pair`, `Output`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `punctuated.rs` vs expected `punctuated.rs`
- **Proposed provenance header:** `// port-lint: source punctuated.rs` (current: `// port-lint: source punctuated.rs`)
- **Lint issues:** 1

### 2. token

- **Target:** `token.Token [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 18
- **Priority Score:** 18142210.0
- **Functions:** 6/17 matched (target 679)
- **Missing functions:** `Group`, `clone`, `fmt`, `eq`, `hash`, `keyword`, `peek_keyword`, `punct`, `punct_helper`, `peek_punct`, `delim`
- **Types:** 2/5 matched (target 307)
- **Missing types:** `Sealed`, `WithSpan`, `CustomToken`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `token.rs` vs expected `token.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `token.rs` vs expected `token.rs`
- **Proposed provenance header:** `// port-lint: source token.rs` (current: `// port-lint: source token.rs`)
- **Proposed provenance header:** `// port-lint: source token.rs` (current: `// port-lint: source token.rs`)
- **Lint issues:** 2

### 3. path

- **Target:** `syn.Path [PROVENANCE-FALLBACK]`
- **Similarity:** 0.32
- **Dependents:** 14
- **Priority Score:** 14182807.0
- **Functions:** 9/26 matched (target 39)
- **Missing functions:** `default`, `const_argument`, `parse_turbofish`, `do_parse`, `parse_helper`, `parse_mod_style`, `parse_rest`, `is_mod_style`, `qpath`, `clone`, `print_path`, `print_path_segment`, `print_path_arguments`, `print_angle_bracketed_generic_arguments`, `print_parenthesized_generic_arguments`, `print_qpath`, `conditionally_print_turbofish`
- **Types:** 1/2 matched (target 25)
- **Missing types:** `QSelfDelimiters`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `path.rs` vs expected `path.rs`
- **Proposed provenance header:** `// port-lint: source path.rs` (current: `// port-lint: source path.rs`)
- **Lint issues:** 2

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

- **Target:** `syn.Expr [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 12
- **Priority Score:** 12656610.0
- **Functions:** 1/65 matched (target 91)
- **Missing functions:** `parse_without_eager_brace`, `parse_with_earlier_boundary_rule`, `peek`, `replace_attrs`, `from`, `eq`, `hash`, `fmt`, `span`, `is_named`, `parse`, `clone`, `parse_expr`, `parse_binop_rhs`, `peek_precedence`, `ambiguous_expr`, `expr_attrs`, `unary_expr`, `trailer_expr`, `trailer_helper`, `atom_expr`, `atom_labeled`, `expr_builtin`, `path_or_macro_or_struct`, `rest_of_path_or_macro_or_struct`, `paren_or_tuple`, `array_or_repeat`, `continue_parsing_early`, `expr_group`, `expr_let`, `expr_unary`, `expr_become`, `expr_closure`, `closure_arg`, `expr_break`, `expr_struct_helper`, `expr_range`, `parse_range_end`, `parse_obsolete`, `parse_multiple`, `multi_index`, `check_cast`, `outer_attrs_to_tokens`, `inner_attrs_to_tokens`, `print_subexpression`, `print_expr`, `print_expr_assign`, `print_expr_await`, `print_expr_binary`, `print_expr_break`, `print_expr_call`, `print_expr_cast`, `print_expr_closure`, `print_expr_field`, `print_expr_index`, `print_expr_let`, `print_expr_method_call`, `print_expr_range`, `print_expr_raw_addr`, `print_expr_reference`, `print_expr_return`, `print_expr_try`, `print_expr_unary`, `print_expr_yield`
- **Types:** 0/1 matched (target 51)
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
- **Similarity:** 0.49
- **Dependents:** 7
- **Priority Score:** 7000105.0
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
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

### 11. gen.fold

- **Target:** `gen.Fold [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 3919110.0
- **Functions:** 0/190 matched (target 0)
- **Missing functions:** `fold_abi`, `fold_angle_bracketed_generic_arguments`, `fold_arm`, `fold_assoc_const`, `fold_assoc_type`, `fold_attr_style`, `fold_attribute`, `fold_attributes`, `fold_bare_fn_arg`, `fold_bare_variadic`, `fold_bin_op`, `fold_block`, `fold_bound_lifetimes`, `fold_captured_param`, `fold_const_param`, `fold_constraint`, `fold_data`, `fold_data_enum`, `fold_data_struct`, `fold_data_union`, `fold_derive_input`, `fold_expr`, `fold_expr_array`, `fold_expr_assign`, `fold_expr_async`, `fold_expr_await`, `fold_expr_binary`, `fold_expr_block`, `fold_expr_break`, `fold_expr_call`, `fold_expr_cast`, `fold_expr_closure`, `fold_expr_const`, `fold_expr_continue`, `fold_expr_field`, `fold_expr_for_loop`, `fold_expr_group`, `fold_expr_if`, `fold_expr_index`, `fold_expr_infer`, `fold_expr_let`, `fold_expr_lit`, `fold_expr_loop`, `fold_expr_macro`, `fold_expr_match`, `fold_expr_method_call`, `fold_expr_paren`, `fold_expr_path`, `fold_expr_range`, `fold_expr_raw_addr`, `fold_expr_reference`, `fold_expr_repeat`, `fold_expr_return`, `fold_expr_struct`, `fold_expr_try`, `fold_expr_try_block`, `fold_expr_tuple`, `fold_expr_unary`, `fold_expr_unsafe`, `fold_expr_while`, `fold_expr_yield`, `fold_field`, `fold_field_mutability`, `fold_field_pat`, `fold_field_value`, `fold_fields`, `fold_fields_named`, `fold_fields_unnamed`, `fold_file`, `fold_fn_arg`, `fold_foreign_item`, `fold_foreign_item_fn`, `fold_foreign_item_macro`, `fold_foreign_item_static`, `fold_foreign_item_type`, `fold_generic_argument`, `fold_generic_param`, `fold_generics`, `fold_ident`, `fold_impl_item`, `fold_impl_item_const`, `fold_impl_item_fn`, `fold_impl_item_macro`, `fold_impl_item_type`, `fold_impl_restriction`, `fold_index`, `fold_item`, `fold_item_const`, `fold_item_enum`, `fold_item_extern_crate`, `fold_item_fn`, `fold_item_foreign_mod`, `fold_item_impl`, `fold_item_macro`, `fold_item_mod`, `fold_item_static`, `fold_item_struct`, `fold_item_trait`, `fold_item_trait_alias`, `fold_item_type`, `fold_item_union`, `fold_item_use`, `fold_label`, `fold_lifetime`, `fold_lifetime_param`, `fold_lit`, `fold_lit_bool`, `fold_lit_byte`, `fold_lit_byte_str`, `fold_lit_cstr`, `fold_lit_char`, `fold_lit_float`, `fold_lit_int`, `fold_lit_str`, `fold_local`, `fold_local_init`, `fold_macro`, `fold_macro_delimiter`, `fold_member`, `fold_meta`, `fold_meta_list`, `fold_meta_name_value`, `fold_parenthesized_generic_arguments`, `fold_pat`, `fold_pat_ident`, `fold_pat_or`, `fold_pat_paren`, `fold_pat_reference`, `fold_pat_rest`, `fold_pat_slice`, `fold_pat_struct`, `fold_pat_tuple`, `fold_pat_tuple_struct`, `fold_pat_type`, `fold_pat_wild`, `fold_path`, `fold_path_arguments`, `fold_path_segment`, `fold_pointer_mutability`, `fold_precise_capture`, `fold_predicate_lifetime`, `fold_predicate_type`, `fold_qself`, `fold_range_limits`, `fold_receiver`, `fold_return_type`, `fold_signature`, `fold_span`, `fold_static_mutability`, `fold_stmt`, `fold_stmt_macro`, `fold_token_stream`, `fold_trait_bound`, `fold_trait_bound_modifier`, `fold_trait_item`, `fold_trait_item_const`, `fold_trait_item_fn`, `fold_trait_item_macro`, `fold_trait_item_type`, `fold_type`, `fold_type_array`, `fold_type_bare_fn`, `fold_type_group`, `fold_type_impl_trait`, `fold_type_infer`, `fold_type_macro`, `fold_type_never`, `fold_type_param`, `fold_type_param_bound`, `fold_type_paren`, `fold_type_path`, `fold_type_ptr`, `fold_type_reference`, `fold_type_slice`, `fold_type_trait_object`, `fold_type_tuple`, `fold_un_op`, `fold_use_glob`, `fold_use_group`, `fold_use_name`, `fold_use_path`, `fold_use_rename`, `fold_use_tree`, `fold_variadic`, `fold_variant`, `fold_vis_restricted`, `fold_visibility`, `fold_where_clause`, `fold_where_predicate`, `fold_vec`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `gen/fold.rs` vs expected `gen/fold.rs`
- **Proposed provenance header:** `// port-lint: source gen/fold.rs` (current: `// port-lint: source gen/fold.rs`)
- **Lint issues:** 1

### 12. item

- **Target:** `syn.Item [PROVENANCE-FALLBACK]`
- **Similarity:** 0.12
- **Dependents:** 3
- **Priority Score:** 3313208.8
- **Functions:** 1/28 matched (target 14)
- **Missing functions:** `replace_attrs`, `from`, `receiver`, `lifetime`, `parse`, `parse_rest_of_item`, `parse_optional_bounds`, `parse_optional_definition`, `parse_macro2`, `parse_item_use`, `parse_use_tree`, `peek_signature`, `parse_signature`, `parse_rest_of_fn`, `parse_fn_arg_or_variadic`, `parse_fn_args`, `parse_foreign_item_type`, `parse_item_type`, `parse_trait_or_trait_alias`, `parse_rest_of_trait`, `parse_start_of_trait_alias`, `parse_rest_of_trait_alias`, `parse_trait_item_type`, `parse_impl`, `parse_impl_item_fn`, `parse_impl_item_type`, `is_inherited`
- **Types:** 0/4 matched (target 17)
- **Missing types:** `FlexibleItemType`, `TypeDefaultness`, `WhereClauseLocation`, `FnArgOrVariadic`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `item.rs` vs expected `item.rs`
- **Proposed provenance header:** `// port-lint: source item.rs` (current: `// port-lint: source item.rs`)
- **Lint issues:** 1

### 13. pat

- **Target:** `syn.Pat [PROVENANCE-FALLBACK]`
- **Similarity:** 0.26
- **Dependents:** 3
- **Priority Score:** 3232407.5
- **Functions:** 1/23 matched (target 22)
- **Missing functions:** `parse_single`, `parse_multi`, `parse_multi_with_leading_vert`, `parse`, `multi_pat_impl`, `pat_path_or_macro_or_struct_or_range`, `pat_wild`, `pat_box`, `pat_ident`, `pat_tuple_struct`, `pat_struct`, `field_pat`, `pat_range`, `pat_range_half_open`, `pat_paren_or_tuple`, `pat_reference`, `pat_lit_or_range`, `into_expr`, `into_pat`, `pat_range_bound`, `pat_slice`, `pat_const`
- **Types:** 0/1 matched (target 12)
- **Missing types:** `PatRangeBound`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `pat.rs` vs expected `pat.rs`
- **Proposed provenance header:** `// port-lint: source pat.rs` (current: `// port-lint: source pat.rs`)
- **Lint issues:** 1

### 14. generics

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

### 15. error

- **Target:** `syn.Error [PROVENANCE-FALLBACK]`
- **Similarity:** 0.35
- **Dependents:** 3
- **Priority Score:** 3112206.5
- **Functions:** 9/14 matched (target 25)
- **Missing functions:** `new2`, `fmt`, `clone`, `into_iter`, `next`
- **Types:** 2/8 matched (target 6)
- **Missing types:** `Result`, `Error`, `_Test`, `Item`, `IntoIter`, `Iter`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `error.rs` vs expected `error.rs`
- **Proposed provenance header:** `// port-lint: source error.rs` (current: `// port-lint: source error.rs`)
- **Lint issues:** 1

### 16. classify

- **Target:** `syn.Classify [PROVENANCE-FALLBACK]`
- **Similarity:** 0.15
- **Dependents:** 3
- **Priority Score:** 3070908.5
- **Functions:** 2/9 matched (target 2)
- **Missing functions:** `trailing_unparameterized_path`, `last_type_in_path`, `last_type_in_bounds`, `expr_leading_label`, `expr_trailing_brace`, `type_trailing_brace`, `tokens_trailing_brace`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `classify.rs` vs expected `classify.rs`
- **Proposed provenance header:** `// port-lint: source classify.rs` (current: `// port-lint: source classify.rs`)
- **Lint issues:** 1

### 17. gen.visit_mut

- **Target:** `gen.VisitMut [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 2909010.0
- **Functions:** 0/189 matched (target 14)
- **Missing functions:** `visit_abi_mut`, `visit_angle_bracketed_generic_arguments_mut`, `visit_arm_mut`, `visit_assoc_const_mut`, `visit_assoc_type_mut`, `visit_attr_style_mut`, `visit_attribute_mut`, `visit_attributes_mut`, `visit_bare_fn_arg_mut`, `visit_bare_variadic_mut`, `visit_bin_op_mut`, `visit_block_mut`, `visit_bound_lifetimes_mut`, `visit_captured_param_mut`, `visit_const_param_mut`, `visit_constraint_mut`, `visit_data_mut`, `visit_data_enum_mut`, `visit_data_struct_mut`, `visit_data_union_mut`, `visit_derive_input_mut`, `visit_expr_mut`, `visit_expr_array_mut`, `visit_expr_assign_mut`, `visit_expr_async_mut`, `visit_expr_await_mut`, `visit_expr_binary_mut`, `visit_expr_block_mut`, `visit_expr_break_mut`, `visit_expr_call_mut`, `visit_expr_cast_mut`, `visit_expr_closure_mut`, `visit_expr_const_mut`, `visit_expr_continue_mut`, `visit_expr_field_mut`, `visit_expr_for_loop_mut`, `visit_expr_group_mut`, `visit_expr_if_mut`, `visit_expr_index_mut`, `visit_expr_infer_mut`, `visit_expr_let_mut`, `visit_expr_lit_mut`, `visit_expr_loop_mut`, `visit_expr_macro_mut`, `visit_expr_match_mut`, `visit_expr_method_call_mut`, `visit_expr_paren_mut`, `visit_expr_path_mut`, `visit_expr_range_mut`, `visit_expr_raw_addr_mut`, `visit_expr_reference_mut`, `visit_expr_repeat_mut`, `visit_expr_return_mut`, `visit_expr_struct_mut`, `visit_expr_try_mut`, `visit_expr_try_block_mut`, `visit_expr_tuple_mut`, `visit_expr_unary_mut`, `visit_expr_unsafe_mut`, `visit_expr_while_mut`, `visit_expr_yield_mut`, `visit_field_mut`, `visit_field_mutability_mut`, `visit_field_pat_mut`, `visit_field_value_mut`, `visit_fields_mut`, `visit_fields_named_mut`, `visit_fields_unnamed_mut`, `visit_file_mut`, `visit_fn_arg_mut`, `visit_foreign_item_mut`, `visit_foreign_item_fn_mut`, `visit_foreign_item_macro_mut`, `visit_foreign_item_static_mut`, `visit_foreign_item_type_mut`, `visit_generic_argument_mut`, `visit_generic_param_mut`, `visit_generics_mut`, `visit_ident_mut`, `visit_impl_item_mut`, `visit_impl_item_const_mut`, `visit_impl_item_fn_mut`, `visit_impl_item_macro_mut`, `visit_impl_item_type_mut`, `visit_impl_restriction_mut`, `visit_index_mut`, `visit_item_mut`, `visit_item_const_mut`, `visit_item_enum_mut`, `visit_item_extern_crate_mut`, `visit_item_fn_mut`, `visit_item_foreign_mod_mut`, `visit_item_impl_mut`, `visit_item_macro_mut`, `visit_item_mod_mut`, `visit_item_static_mut`, `visit_item_struct_mut`, `visit_item_trait_mut`, `visit_item_trait_alias_mut`, `visit_item_type_mut`, `visit_item_union_mut`, `visit_item_use_mut`, `visit_label_mut`, `visit_lifetime_mut`, `visit_lifetime_param_mut`, `visit_lit_mut`, `visit_lit_bool_mut`, `visit_lit_byte_mut`, `visit_lit_byte_str_mut`, `visit_lit_cstr_mut`, `visit_lit_char_mut`, `visit_lit_float_mut`, `visit_lit_int_mut`, `visit_lit_str_mut`, `visit_local_mut`, `visit_local_init_mut`, `visit_macro_mut`, `visit_macro_delimiter_mut`, `visit_member_mut`, `visit_meta_mut`, `visit_meta_list_mut`, `visit_meta_name_value_mut`, `visit_parenthesized_generic_arguments_mut`, `visit_pat_mut`, `visit_pat_ident_mut`, `visit_pat_or_mut`, `visit_pat_paren_mut`, `visit_pat_reference_mut`, `visit_pat_rest_mut`, `visit_pat_slice_mut`, `visit_pat_struct_mut`, `visit_pat_tuple_mut`, `visit_pat_tuple_struct_mut`, `visit_pat_type_mut`, `visit_pat_wild_mut`, `visit_path_mut`, `visit_path_arguments_mut`, `visit_path_segment_mut`, `visit_pointer_mutability_mut`, `visit_precise_capture_mut`, `visit_predicate_lifetime_mut`, `visit_predicate_type_mut`, `visit_qself_mut`, `visit_range_limits_mut`, `visit_receiver_mut`, `visit_return_type_mut`, `visit_signature_mut`, `visit_span_mut`, `visit_static_mutability_mut`, `visit_stmt_mut`, `visit_stmt_macro_mut`, `visit_token_stream_mut`, `visit_trait_bound_mut`, `visit_trait_bound_modifier_mut`, `visit_trait_item_mut`, `visit_trait_item_const_mut`, `visit_trait_item_fn_mut`, `visit_trait_item_macro_mut`, `visit_trait_item_type_mut`, `visit_type_mut`, `visit_type_array_mut`, `visit_type_bare_fn_mut`, `visit_type_group_mut`, `visit_type_impl_trait_mut`, `visit_type_infer_mut`, `visit_type_macro_mut`, `visit_type_never_mut`, `visit_type_param_mut`, `visit_type_param_bound_mut`, `visit_type_paren_mut`, `visit_type_path_mut`, `visit_type_ptr_mut`, `visit_type_reference_mut`, `visit_type_slice_mut`, `visit_type_trait_object_mut`, `visit_type_tuple_mut`, `visit_un_op_mut`, `visit_use_glob_mut`, `visit_use_group_mut`, `visit_use_name_mut`, `visit_use_path_mut`, `visit_use_rename_mut`, `visit_use_tree_mut`, `visit_variadic_mut`, `visit_variant_mut`, `visit_vis_restricted_mut`, `visit_visibility_mut`, `visit_where_clause_mut`, `visit_where_predicate_mut`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `gen/visit_mut.rs` vs expected `gen/visit_mut.rs`
- **Proposed provenance header:** `// port-lint: source gen/visit_mut.rs` (current: `// port-lint: source gen/visit_mut.rs`)
- **Lint issues:** 1

### 18. gen.visit

- **Target:** `gen.Visit [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 2758910.0
- **Functions:** 14/188 matched (target 14)
- **Missing functions:** `visit_abi`, `visit_angle_bracketed_generic_arguments`, `visit_arm`, `visit_assoc_const`, `visit_assoc_type`, `visit_attr_style`, `visit_bare_fn_arg`, `visit_bare_variadic`, `visit_bin_op`, `visit_block`, `visit_bound_lifetimes`, `visit_captured_param`, `visit_const_param`, `visit_constraint`, `visit_data_enum`, `visit_data_struct`, `visit_data_union`, `visit_expr_array`, `visit_expr_assign`, `visit_expr_async`, `visit_expr_await`, `visit_expr_binary`, `visit_expr_block`, `visit_expr_break`, `visit_expr_call`, `visit_expr_cast`, `visit_expr_closure`, `visit_expr_const`, `visit_expr_continue`, `visit_expr_field`, `visit_expr_for_loop`, `visit_expr_group`, `visit_expr_if`, `visit_expr_index`, `visit_expr_infer`, `visit_expr_let`, `visit_expr_lit`, `visit_expr_loop`, `visit_expr_macro`, `visit_expr_match`, `visit_expr_method_call`, `visit_expr_paren`, `visit_expr_path`, `visit_expr_range`, `visit_expr_raw_addr`, `visit_expr_reference`, `visit_expr_repeat`, `visit_expr_return`, `visit_expr_struct`, `visit_expr_try`, `visit_expr_try_block`, `visit_expr_tuple`, `visit_expr_unary`, `visit_expr_unsafe`, `visit_expr_while`, `visit_expr_yield`, `visit_field`, `visit_field_mutability`, `visit_field_pat`, `visit_field_value`, `visit_fields`, `visit_fields_named`, `visit_fields_unnamed`, `visit_file`, `visit_fn_arg`, `visit_foreign_item`, `visit_foreign_item_fn`, `visit_foreign_item_macro`, `visit_foreign_item_static`, `visit_foreign_item_type`, `visit_generic_argument`, `visit_generic_param`, `visit_impl_item`, `visit_impl_item_const`, `visit_impl_item_fn`, `visit_impl_item_macro`, `visit_impl_item_type`, `visit_impl_restriction`, `visit_index`, `visit_item_const`, `visit_item_enum`, `visit_item_extern_crate`, `visit_item_fn`, `visit_item_foreign_mod`, `visit_item_impl`, `visit_item_macro`, `visit_item_mod`, `visit_item_static`, `visit_item_struct`, `visit_item_trait`, `visit_item_trait_alias`, `visit_item_type`, `visit_item_union`, `visit_item_use`, `visit_label`, `visit_lifetime_param`, `visit_lit_bool`, `visit_lit_byte`, `visit_lit_byte_str`, `visit_lit_cstr`, `visit_lit_char`, `visit_lit_float`, `visit_lit_int`, `visit_lit_str`, `visit_local`, `visit_local_init`, `visit_macro`, `visit_macro_delimiter`, `visit_member`, `visit_meta_list`, `visit_meta_name_value`, `visit_parenthesized_generic_arguments`, `visit_pat_ident`, `visit_pat_or`, `visit_pat_paren`, `visit_pat_reference`, `visit_pat_rest`, `visit_pat_slice`, `visit_pat_struct`, `visit_pat_tuple`, `visit_pat_tuple_struct`, `visit_pat_type`, `visit_pat_wild`, `visit_path_arguments`, `visit_path_segment`, `visit_pointer_mutability`, `visit_precise_capture`, `visit_predicate_lifetime`, `visit_predicate_type`, `visit_qself`, `visit_range_limits`, `visit_receiver`, `visit_return_type`, `visit_signature`, `visit_span`, `visit_static_mutability`, `visit_stmt_macro`, `visit_token_stream`, `visit_trait_bound`, `visit_trait_bound_modifier`, `visit_trait_item`, `visit_trait_item_const`, `visit_trait_item_fn`, `visit_trait_item_macro`, `visit_trait_item_type`, `visit_type_array`, `visit_type_bare_fn`, `visit_type_group`, `visit_type_impl_trait`, `visit_type_infer`, `visit_type_macro`, `visit_type_never`, `visit_type_param`, `visit_type_param_bound`, `visit_type_paren`, `visit_type_path`, `visit_type_ptr`, `visit_type_reference`, `visit_type_slice`, `visit_type_trait_object`, `visit_type_tuple`, `visit_un_op`, `visit_use_glob`, `visit_use_group`, `visit_use_name`, `visit_use_path`, `visit_use_rename`, `visit_use_tree`, `visit_variadic`, `visit_variant`, `visit_vis_restricted`, `visit_visibility`, `visit_where_clause`, `visit_where_predicate`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `gen/visit.rs` vs expected `gen/visit.rs`
- **Proposed provenance header:** `// port-lint: source gen/visit.rs` (current: `// port-lint: source gen/visit.rs`)
- **Lint issues:** 15

### 19. ty

- **Target:** `syn.Type [PROVENANCE-FALLBACK]`
- **Similarity:** 0.30
- **Dependents:** 2
- **Priority Score:** 2060707.0
- **Functions:** 1/7 matched (target 39)
- **Missing functions:** `parse`, `without_plus`, `ambig_ty`, `parse_bounds`, `parse_bare_fn_arg`, `parse_bare_variadic`
- **Types:** 0/0 matched (target 23)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `ty.rs` vs expected `ty.rs`
- **Proposed provenance header:** `// port-lint: source ty.rs` (current: `// port-lint: source ty.rs`)
- **Lint issues:** 2

### 20. precedence

- **Target:** `syn.Precedence [PROVENANCE-FALLBACK]`
- **Similarity:** 0.25
- **Dependents:** 2
- **Priority Score:** 2040707.5
- **Functions:** 2/6 matched (target 2)
- **Missing functions:** `of`, `clone`, `eq`, `partial_cmp`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `precedence.rs` vs expected `precedence.rs`
- **Proposed provenance header:** `// port-lint: source precedence.rs` (current: `// port-lint: source precedence.rs`)
- **Lint issues:** 1

### 21. spanned

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

### 22. attr

- **Target:** `syn.Attr [PROVENANCE-FALLBACK]`
- **Similarity:** 0.17
- **Dependents:** 1
- **Priority Score:** 1242708.2
- **Functions:** 3/23 matched (target 17)
- **Missing functions:** `parse_args`, `parse_args_with`, `parse_outer`, `parse_inner`, `require_path_only`, `require_list`, `require_name_value`, `outer`, `is_outer`, `inner`, `is_inner`, `from`, `single_parse_inner`, `single_parse_outer`, `parse`, `parse_outermost_meta_path`, `parse_meta_after_path`, `parse_meta_list_after_path`, `parse_meta_name_value_after_path`, `fmt`
- **Types:** 0/4 matched (target 9)
- **Missing types:** `FilterAttrs`, `Ret`, `DisplayAttrStyle`, `DisplayPath`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `attr.rs` vs expected `attr.rs`
- **Proposed provenance header:** `// port-lint: source attr.rs` (current: `// port-lint: source attr.rs`)
- **Lint issues:** 2

### 23. parse

- **Target:** `syn.Parse [PROVENANCE-FALLBACK]`
- **Similarity:** 0.46
- **Dependents:** 1
- **Priority Score:** 1104205.4
- **Functions:** 25/33 matched (target 54)
- **Missing functions:** `drop`, `fmt`, `deref`, `default`, `cell_clone`, `to_tokens`, `eq`, `hash`
- **Types:** 7/9 matched (target 16)
- **Missing types:** `Target`, `Output`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `parse.rs` vs expected `parse.rs`
- **Proposed provenance header:** `// port-lint: source parse.rs` (current: `// port-lint: source parse.rs`)
- **Lint issues:** 2

### 24. stmt

- **Target:** `syn.Stmt [PROVENANCE-FALLBACK]`
- **Similarity:** 0.29
- **Dependents:** 1
- **Priority Score:** 1070807.1
- **Functions:** 1/7 matched (target 12)
- **Missing functions:** `parse_within`, `parse`, `parse_stmt`, `stmt_mac`, `stmt_local`, `stmt_expr`
- **Types:** 0/1 matched (target 7)
- **Missing types:** `AllowNoSemi`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `stmt.rs` vs expected `stmt.rs`
- **Proposed provenance header:** `// port-lint: source stmt.rs` (current: `// port-lint: source stmt.rs`)
- **Lint issues:** 1

### 25. bigint

- **Target:** `syn.BigInt [PROVENANCE-FALLBACK]`
- **Similarity:** 0.46
- **Dependents:** 1
- **Priority Score:** 1020605.4
- **Functions:** 3/5 matched (target 6)
- **Missing functions:** `add_assign`, `mul_assign`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `bigint.rs` vs expected `bigint.rs`
- **Proposed provenance header:** `// port-lint: source bigint.rs` (current: `// port-lint: source bigint.rs`)
- **Lint issues:** 1

### 26. gen.debug

- **Target:** `gen.Debug [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1020210.0
- **Functions:** 0/2 matched (target 0)
- **Missing functions:** `fmt`, `debug`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `gen/debug.rs` vs expected `gen/debug.rs`
- **Proposed provenance header:** `// port-lint: source gen/debug.rs` (current: `// port-lint: source gen/debug.rs`)
- **Lint issues:** 1

### 27. group

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

### 28. file

- **Target:** `syn.File [PROVENANCE-FALLBACK]`
- **Similarity:** 0.31
- **Dependents:** 1
- **Priority Score:** 1010206.9
- **Functions:** 1/2 matched (target 1)
- **Missing functions:** `parse`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `file.rs` vs expected `file.rs`
- **Proposed provenance header:** `// port-lint: source file.rs` (current: `// port-lint: source file.rs`)
- **Lint issues:** 1

### 29. gen.clone

- **Target:** `gen.Clone [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1010110.0
- **Functions:** 0/1 matched (target 0)
- **Missing functions:** `clone`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `gen/clone.rs` vs expected `gen/clone.rs`
- **Proposed provenance header:** `// port-lint: source gen/clone.rs` (current: `// port-lint: source gen/clone.rs`)
- **Lint issues:** 1

### 30. sealed

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

### 31. data

- **Target:** `syn.Data [PROVENANCE-FALLBACK]`
- **Similarity:** 0.25
- **Dependents:** 0
- **Priority Score:** 111407.5
- **Functions:** 3/11 matched (target 19)
- **Missing functions:** `iter`, `iter_mut`, `into_iter`, `next`, `clone`, `parse`, `parse_named`, `parse_unnamed`
- **Types:** 0/3 matched (target 16)
- **Missing types:** `Item`, `IntoIter`, `Members`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `data.rs` vs expected `data.rs`
- **Proposed provenance header:** `// port-lint: source data.rs` (current: `// port-lint: source data.rs`)
- **Lint issues:** 2

### 32. ext

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

### 33. drops

- **Target:** `syn.Drops [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 80910.0
- **Functions:** 0/5 matched (target 0)
- **Missing functions:** `new`, `deref`, `deref_mut`, `test_needs_drop`, `drop`
- **Types:** 1/4 matched (target 1)
- **Missing types:** `Target`, `TrivialDrop`, `NeedsDrop`
- **Tests:** 0/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `drops.rs` vs expected `drops.rs`
- **Proposed provenance header:** `// port-lint: source drops.rs` (current: `// port-lint: source drops.rs`)
- **Lint issues:** 1

### 34. export

- **Target:** `syn.Export [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 80800.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/8 matched (target 0)
- **Missing types:** `Formatter`, `FmtResult`, `bool`, `str`, `Span`, `TokenStream2`, `TokenStream`, `private`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `export.rs` vs expected `export.rs`
- **Proposed provenance header:** `// port-lint: source export.rs` (current: `// port-lint: source export.rs`)
- **Lint issues:** 1

### 35. mac

- **Target:** `syn.Mac [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 70810.0
- **Functions:** 1/8 matched (target 2)
- **Missing functions:** `span`, `is_brace`, `parse_body`, `parse_body_with`, `parse_delimiter`, `parse`, `surround`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `mac.rs` vs expected `mac.rs`
- **Proposed provenance header:** `// port-lint: source mac.rs` (current: `// port-lint: source mac.rs`)
- **Lint issues:** 1

### 36. meta

- **Target:** `syn.Meta [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 60610.0
- **Functions:** 0/5 matched (target 2)
- **Missing functions:** `parser`, `value`, `parse_nested_meta`, `error`, `parse_meta_path`
- **Types:** 0/1 matched (target 0)
- **Missing types:** `ParseNestedMeta`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `meta.rs` vs expected `meta.rs`
- **Proposed provenance header:** `// port-lint: source meta.rs` (current: `// port-lint: source meta.rs`)
- **Lint issues:** 1

### 37. buffer

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

### 38. tt

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

### 39. thread

- **Target:** `syn.Thread [PROVENANCE-FALLBACK]`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 20507.0
- **Functions:** 2/4 matched (target 6)
- **Missing functions:** `fmt`, `clone`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `thread.rs` vs expected `thread.rs`
- **Proposed provenance header:** `// port-lint: source thread.rs` (current: `// port-lint: source thread.rs`)
- **Lint issues:** 1

### 40. discouraged

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

### 41. restriction

- **Target:** `syn.Restriction [PROVENANCE-FALLBACK]`
- **Similarity:** 0.56
- **Dependents:** 0
- **Priority Score:** 10404.4
- **Functions:** 3/4 matched (target 15)
- **Missing functions:** `parse_pub`
- **Types:** 0/0 matched (target 13)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `restriction.rs` vs expected `restriction.rs`
- **Proposed provenance header:** `// port-lint: source restriction.rs` (current: `// port-lint: source restriction.rs`)
- **Lint issues:** 3

### 42. gen.hash

- **Target:** `gen.Hash [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10110.0
- **Functions:** 0/1 matched (target 0)
- **Missing functions:** `hash`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `gen/hash.rs` vs expected `gen/hash.rs`
- **Proposed provenance header:** `// port-lint: source gen/hash.rs` (current: `// port-lint: source gen/hash.rs`)
- **Lint issues:** 1

### 43. gen.eq

- **Target:** `gen.Eq [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10110.0
- **Functions:** 0/1 matched (target 0)
- **Missing functions:** `eq`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `gen/eq.rs` vs expected `gen/eq.rs`
- **Proposed provenance header:** `// port-lint: source gen/eq.rs` (current: `// port-lint: source gen/eq.rs`)
- **Lint issues:** 1

### 44. parse_quote

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

### 45. whitespace

- **Target:** `syn.Whitespace [PROVENANCE-FALLBACK]`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 204.9
- **Functions:** 2/2 matched (target 5)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `whitespace.rs` vs expected `whitespace.rs`
- **Proposed provenance header:** `// port-lint: source whitespace.rs` (current: `// port-lint: source whitespace.rs`)
- **Lint issues:** 1

### 46. op

- **Target:** `syn.Op [PROVENANCE-FALLBACK]`
- **Similarity:** 0.55
- **Dependents:** 0
- **Priority Score:** 204.5
- **Functions:** 2/2 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 35)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `op.rs` vs expected `op.rs`
- **Proposed provenance header:** `// port-lint: source op.rs` (current: `// port-lint: source op.rs`)
- **Lint issues:** 1

### 47. print

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

### 48. parse_macro_input

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

