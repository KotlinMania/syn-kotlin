# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 53/55 (96.4%)
- **Function parity:** 240/1083 matched (target 1819) — 22.2%
- **Class/type parity:** 41/121 matched (target 718) — 33.9%
- **Combined symbol parity:** 281/1204 matched (target 2537) — 23.3%
- **Average inline-code cosine:** 0.38 (function body across 53 matched files)
- **Average documentation cosine:** 0.32 (doc text across 53 matched files)
- **Cheat-zeroed Files:** 10
- **Critical Issues:** 37 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. token
- **Similarity:** 0.00 (needs 85% improvement)
- **Dependencies:** 17
- **Priority Score:** 17082210.0
- **Functions:** 12/17 matched (target 680)
- **Missing functions:** `Group`, `clone`, `fmt`, `eq`, `hash`
- **Types:** 2/5 matched (target 302)
- **Missing types:** `Sealed`, `WithSpan`, `CustomToken`
- **Symbol Deficit:** 8 (functions: 5, types: 3)
- **Action:** Deep review - likely missing major functionality

### 2. punctuated
- **Similarity:** 0.13 (needs 72% improvement)
- **Dependencies:** 13
- **Priority Score:** 13436809.0
- **Functions:** 24/54 matched (target 203)
- **Missing functions:** `first_mut`, `last_mut`, `get_mut`, `iter_mut`, `pairs_mut`, `insert`, `parse_terminated`, `parse_separated_nonempty`, `parse_separated_nonempty_with`, `clone`, `clone_from`, `eq`, `hash`, `fmt`, `from_iter`, `do_extend`, `into_iter`, `default`, `next`, `size_hint`, `next_back`, `empty_punctuated_iter`, `clone_box`, `empty_punctuated_iter_mut`, `value_mut`, `punct_mut`, `into_tuple`, `cloned`, `index`, `index_mut`
- **Types:** 1/14 matched (target 19)
- **Missing types:** `Item`, `IntoIter`, `Pairs`, `PairsMut`, `IntoPairs`, `Iter`, `IterTrait`, `PrivateIter`, `IterMut`, `IterMutTrait`, `PrivateIterMut`, `Pair`, `Output`
- **Symbol Deficit:** 43 (functions: 30, types: 13)
- **Action:** Deep review - likely missing major functionality

### 3. expr
- **Similarity:** 0.21 (needs 64% improvement)
- **Dependencies:** 10
- **Priority Score:** 10656608.0
- **Functions:** 1/65 matched (target 127)
- **Missing functions:** `parse_without_eager_brace`, `parse_with_earlier_boundary_rule`, `peek`, `replace_attrs`, `from`, `eq`, `hash`, `fmt`, `span`, `is_named`, `parse`, `clone`, `parse_expr`, `parse_binop_rhs`, `peek_precedence`, `ambiguous_expr`, `expr_attrs`, `unary_expr`, `trailer_expr`, `trailer_helper`, `atom_expr`, `atom_labeled`, `expr_builtin`, `path_or_macro_or_struct`, `rest_of_path_or_macro_or_struct`, `paren_or_tuple`, `array_or_repeat`, `continue_parsing_early`, `expr_group`, `expr_let`, `expr_unary`, `expr_become`, `expr_closure`, `closure_arg`, `expr_break`, `expr_struct_helper`, `expr_range`, `parse_range_end`, `parse_obsolete`, `parse_multiple`, `multi_index`, `check_cast`, `outer_attrs_to_tokens`, `inner_attrs_to_tokens`, `print_subexpression`, `print_expr`, `print_expr_assign`, `print_expr_await`, `print_expr_binary`, `print_expr_break`, `print_expr_call`, `print_expr_cast`, `print_expr_closure`, `print_expr_field`, `print_expr_index`, `print_expr_let`, `print_expr_method_call`, `print_expr_range`, `print_expr_raw_addr`, `print_expr_reference`, `print_expr_return`, `print_expr_try`, `print_expr_unary`, `print_expr_yield`
- **Types:** 0/1 matched (target 53)
- **Missing types:** `AllowStruct`
- **Symbol Deficit:** 65 (functions: 64, types: 1)
- **Action:** Deep review - likely missing major functionality

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. token

- **Target:** `token.Token [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 17
- **Priority Score:** 17082210.0
- **Functions:** 12/17 matched (target 680)
- **Missing functions:** `Group`, `clone`, `fmt`, `eq`, `hash`
- **Types:** 2/5 matched (target 302)
- **Missing types:** `Sealed`, `WithSpan`, `CustomToken`

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
- **Similarity:** 0.13
- **Dependents:** 13
- **Priority Score:** 13436809.0
- **Functions:** 24/54 matched (target 203)
- **Missing functions:** `first_mut`, `last_mut`, `get_mut`, `iter_mut`, `pairs_mut`, `insert`, `parse_terminated`, `parse_separated_nonempty`, `parse_separated_nonempty_with`, `clone`, `clone_from`, `eq`, `hash`, `fmt`, `from_iter`, `do_extend`, `into_iter`, `default`, `next`, `size_hint`, `next_back`, `empty_punctuated_iter`, `clone_box`, `empty_punctuated_iter_mut`, `value_mut`, `punct_mut`, `into_tuple`, `cloned`, `index`, `index_mut`
- **Types:** 1/14 matched (target 19)
- **Missing types:** `Item`, `IntoIter`, `Pairs`, `PairsMut`, `IntoPairs`, `Iter`, `IterTrait`, `PrivateIter`, `IterMut`, `IterMutTrait`, `PrivateIterMut`, `Pair`, `Output`

### 4. expr

- **Target:** `syn.Expr`
- **Similarity:** 0.21
- **Dependents:** 10
- **Priority Score:** 10656608.0
- **Functions:** 1/65 matched (target 127)
- **Missing functions:** `parse_without_eager_brace`, `parse_with_earlier_boundary_rule`, `peek`, `replace_attrs`, `from`, `eq`, `hash`, `fmt`, `span`, `is_named`, `parse`, `clone`, `parse_expr`, `parse_binop_rhs`, `peek_precedence`, `ambiguous_expr`, `expr_attrs`, `unary_expr`, `trailer_expr`, `trailer_helper`, `atom_expr`, `atom_labeled`, `expr_builtin`, `path_or_macro_or_struct`, `rest_of_path_or_macro_or_struct`, `paren_or_tuple`, `array_or_repeat`, `continue_parsing_early`, `expr_group`, `expr_let`, `expr_unary`, `expr_become`, `expr_closure`, `closure_arg`, `expr_break`, `expr_struct_helper`, `expr_range`, `parse_range_end`, `parse_obsolete`, `parse_multiple`, `multi_index`, `check_cast`, `outer_attrs_to_tokens`, `inner_attrs_to_tokens`, `print_subexpression`, `print_expr`, `print_expr_assign`, `print_expr_await`, `print_expr_binary`, `print_expr_break`, `print_expr_call`, `print_expr_cast`, `print_expr_closure`, `print_expr_field`, `print_expr_index`, `print_expr_let`, `print_expr_method_call`, `print_expr_range`, `print_expr_raw_addr`, `print_expr_reference`, `print_expr_return`, `print_expr_try`, `print_expr_unary`, `print_expr_yield`
- **Types:** 0/1 matched (target 53)
- **Missing types:** `AllowStruct`

### 5. path

- **Target:** `syn.Path`
- **Similarity:** 0.33
- **Dependents:** 9
- **Priority Score:** 9182807.0
- **Functions:** 9/26 matched (target 44)
- **Missing functions:** `default`, `const_argument`, `parse_turbofish`, `do_parse`, `parse_helper`, `parse_mod_style`, `parse_rest`, `is_mod_style`, `qpath`, `clone`, `print_path`, `print_path_segment`, `print_path_arguments`, `print_angle_bracketed_generic_arguments`, `print_parenthesized_generic_arguments`, `print_qpath`, `conditionally_print_turbofish`
- **Types:** 1/2 matched (target 25)
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

### 8. lit

- **Target:** `syn.Lit`
- **Similarity:** 0.13
- **Dependents:** 5
- **Priority Score:** 5334208.5
- **Functions:** 9/38 matched (target 44)
- **Missing functions:** `parse_with`, `respan_token_stream`, `respan_token_tree`, `set_span`, `suffix`, `from`, `fmt`, `debug`, `clone`, `parse_negative_lit`, `peek_impl`, `from_str_for_fuzzing`, `from_str`, `byte`, `next_chr`, `parse_lit_str`, `parse_lit_str_cooked`, `parse_lit_str_raw`, `parse_lit_byte_str`, `parse_lit_byte_str_cooked`, `parse_lit_byte_str_raw`, `parse_lit_c_str`, `parse_lit_c_str_cooked`, `parse_lit_c_str_raw`, `parse_lit_char`, `backslash_x`, `backslash_u`, `parse_lit_int`, `parse_lit_float`
- **Types:** 0/4 matched (target 22)
- **Missing types:** `LitRepr`, `LitIntRepr`, `LitFloatRepr`, `StrStyle`

### 9. lookahead

- **Target:** `syn.Lookahead`
- **Similarity:** 0.44
- **Dependents:** 5
- **Priority Score:** 5061405.5
- **Functions:** 4/8 matched (target 18)
- **Missing functions:** `new`, `peek_impl`, `fmt`, `clone`
- **Types:** 4/6 matched (target 11)
- **Missing types:** `CommaSeparated`, `Token`

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
- **Similarity:** 0.42
- **Dependents:** 3
- **Priority Score:** 3082205.8
- **Functions:** 12/14 matched (target 28)
- **Missing functions:** `fmt`, `next`
- **Types:** 2/8 matched (target 6)
- **Missing types:** `Result`, `Error`, `_Test`, `Item`, `IntoIter`, `Iter`

### 12. classify

- **Target:** `syn.Classify`
- **Similarity:** 0.16
- **Dependents:** 3
- **Priority Score:** 3070908.2
- **Functions:** 2/9 matched (target 2)
- **Missing functions:** `trailing_unparameterized_path`, `last_type_in_path`, `last_type_in_bounds`, `expr_leading_label`, `expr_trailing_brace`, `type_trailing_brace`, `tokens_trailing_brace`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 13. gen.fold

- **Target:** `gen.Fold [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 2919110.0
- **Functions:** 0/190 matched (target 0)
- **Missing functions:** `fold_abi`, `fold_angle_bracketed_generic_arguments`, `fold_arm`, `fold_assoc_const`, `fold_assoc_type`, `fold_attr_style`, `fold_attribute`, `fold_attributes`, `fold_bare_fn_arg`, `fold_bare_variadic`, `fold_bin_op`, `fold_block`, `fold_bound_lifetimes`, `fold_captured_param`, `fold_const_param`, `fold_constraint`, `fold_data`, `fold_data_enum`, `fold_data_struct`, `fold_data_union`, `fold_derive_input`, `fold_expr`, `fold_expr_array`, `fold_expr_assign`, `fold_expr_async`, `fold_expr_await`, `fold_expr_binary`, `fold_expr_block`, `fold_expr_break`, `fold_expr_call`, `fold_expr_cast`, `fold_expr_closure`, `fold_expr_const`, `fold_expr_continue`, `fold_expr_field`, `fold_expr_for_loop`, `fold_expr_group`, `fold_expr_if`, `fold_expr_index`, `fold_expr_infer`, `fold_expr_let`, `fold_expr_lit`, `fold_expr_loop`, `fold_expr_macro`, `fold_expr_match`, `fold_expr_method_call`, `fold_expr_paren`, `fold_expr_path`, `fold_expr_range`, `fold_expr_raw_addr`, `fold_expr_reference`, `fold_expr_repeat`, `fold_expr_return`, `fold_expr_struct`, `fold_expr_try`, `fold_expr_try_block`, `fold_expr_tuple`, `fold_expr_unary`, `fold_expr_unsafe`, `fold_expr_while`, `fold_expr_yield`, `fold_field`, `fold_field_mutability`, `fold_field_pat`, `fold_field_value`, `fold_fields`, `fold_fields_named`, `fold_fields_unnamed`, `fold_file`, `fold_fn_arg`, `fold_foreign_item`, `fold_foreign_item_fn`, `fold_foreign_item_macro`, `fold_foreign_item_static`, `fold_foreign_item_type`, `fold_generic_argument`, `fold_generic_param`, `fold_generics`, `fold_ident`, `fold_impl_item`, `fold_impl_item_const`, `fold_impl_item_fn`, `fold_impl_item_macro`, `fold_impl_item_type`, `fold_impl_restriction`, `fold_index`, `fold_item`, `fold_item_const`, `fold_item_enum`, `fold_item_extern_crate`, `fold_item_fn`, `fold_item_foreign_mod`, `fold_item_impl`, `fold_item_macro`, `fold_item_mod`, `fold_item_static`, `fold_item_struct`, `fold_item_trait`, `fold_item_trait_alias`, `fold_item_type`, `fold_item_union`, `fold_item_use`, `fold_label`, `fold_lifetime`, `fold_lifetime_param`, `fold_lit`, `fold_lit_bool`, `fold_lit_byte`, `fold_lit_byte_str`, `fold_lit_cstr`, `fold_lit_char`, `fold_lit_float`, `fold_lit_int`, `fold_lit_str`, `fold_local`, `fold_local_init`, `fold_macro`, `fold_macro_delimiter`, `fold_member`, `fold_meta`, `fold_meta_list`, `fold_meta_name_value`, `fold_parenthesized_generic_arguments`, `fold_pat`, `fold_pat_ident`, `fold_pat_or`, `fold_pat_paren`, `fold_pat_reference`, `fold_pat_rest`, `fold_pat_slice`, `fold_pat_struct`, `fold_pat_tuple`, `fold_pat_tuple_struct`, `fold_pat_type`, `fold_pat_wild`, `fold_path`, `fold_path_arguments`, `fold_path_segment`, `fold_pointer_mutability`, `fold_precise_capture`, `fold_predicate_lifetime`, `fold_predicate_type`, `fold_qself`, `fold_range_limits`, `fold_receiver`, `fold_return_type`, `fold_signature`, `fold_span`, `fold_static_mutability`, `fold_stmt`, `fold_stmt_macro`, `fold_token_stream`, `fold_trait_bound`, `fold_trait_bound_modifier`, `fold_trait_item`, `fold_trait_item_const`, `fold_trait_item_fn`, `fold_trait_item_macro`, `fold_trait_item_type`, `fold_type`, `fold_type_array`, `fold_type_bare_fn`, `fold_type_group`, `fold_type_impl_trait`, `fold_type_infer`, `fold_type_macro`, `fold_type_never`, `fold_type_param`, `fold_type_param_bound`, `fold_type_paren`, `fold_type_path`, `fold_type_ptr`, `fold_type_reference`, `fold_type_slice`, `fold_type_trait_object`, `fold_type_tuple`, `fold_un_op`, `fold_use_glob`, `fold_use_group`, `fold_use_name`, `fold_use_path`, `fold_use_rename`, `fold_use_tree`, `fold_variadic`, `fold_variant`, `fold_vis_restricted`, `fold_visibility`, `fold_where_clause`, `fold_where_predicate`, `fold_vec`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 14. gen.visit_mut

- **Target:** `gen.VisitMut`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 2909010.0
- **Functions:** 0/189 matched (target 45)
- **Missing functions:** `visit_abi_mut`, `visit_angle_bracketed_generic_arguments_mut`, `visit_arm_mut`, `visit_assoc_const_mut`, `visit_assoc_type_mut`, `visit_attr_style_mut`, `visit_attribute_mut`, `visit_attributes_mut`, `visit_bare_fn_arg_mut`, `visit_bare_variadic_mut`, `visit_bin_op_mut`, `visit_block_mut`, `visit_bound_lifetimes_mut`, `visit_captured_param_mut`, `visit_const_param_mut`, `visit_constraint_mut`, `visit_data_mut`, `visit_data_enum_mut`, `visit_data_struct_mut`, `visit_data_union_mut`, `visit_derive_input_mut`, `visit_expr_mut`, `visit_expr_array_mut`, `visit_expr_assign_mut`, `visit_expr_async_mut`, `visit_expr_await_mut`, `visit_expr_binary_mut`, `visit_expr_block_mut`, `visit_expr_break_mut`, `visit_expr_call_mut`, `visit_expr_cast_mut`, `visit_expr_closure_mut`, `visit_expr_const_mut`, `visit_expr_continue_mut`, `visit_expr_field_mut`, `visit_expr_for_loop_mut`, `visit_expr_group_mut`, `visit_expr_if_mut`, `visit_expr_index_mut`, `visit_expr_infer_mut`, `visit_expr_let_mut`, `visit_expr_lit_mut`, `visit_expr_loop_mut`, `visit_expr_macro_mut`, `visit_expr_match_mut`, `visit_expr_method_call_mut`, `visit_expr_paren_mut`, `visit_expr_path_mut`, `visit_expr_range_mut`, `visit_expr_raw_addr_mut`, `visit_expr_reference_mut`, `visit_expr_repeat_mut`, `visit_expr_return_mut`, `visit_expr_struct_mut`, `visit_expr_try_mut`, `visit_expr_try_block_mut`, `visit_expr_tuple_mut`, `visit_expr_unary_mut`, `visit_expr_unsafe_mut`, `visit_expr_while_mut`, `visit_expr_yield_mut`, `visit_field_mut`, `visit_field_mutability_mut`, `visit_field_pat_mut`, `visit_field_value_mut`, `visit_fields_mut`, `visit_fields_named_mut`, `visit_fields_unnamed_mut`, `visit_file_mut`, `visit_fn_arg_mut`, `visit_foreign_item_mut`, `visit_foreign_item_fn_mut`, `visit_foreign_item_macro_mut`, `visit_foreign_item_static_mut`, `visit_foreign_item_type_mut`, `visit_generic_argument_mut`, `visit_generic_param_mut`, `visit_generics_mut`, `visit_ident_mut`, `visit_impl_item_mut`, `visit_impl_item_const_mut`, `visit_impl_item_fn_mut`, `visit_impl_item_macro_mut`, `visit_impl_item_type_mut`, `visit_impl_restriction_mut`, `visit_index_mut`, `visit_item_mut`, `visit_item_const_mut`, `visit_item_enum_mut`, `visit_item_extern_crate_mut`, `visit_item_fn_mut`, `visit_item_foreign_mod_mut`, `visit_item_impl_mut`, `visit_item_macro_mut`, `visit_item_mod_mut`, `visit_item_static_mut`, `visit_item_struct_mut`, `visit_item_trait_mut`, `visit_item_trait_alias_mut`, `visit_item_type_mut`, `visit_item_union_mut`, `visit_item_use_mut`, `visit_label_mut`, `visit_lifetime_mut`, `visit_lifetime_param_mut`, `visit_lit_mut`, `visit_lit_bool_mut`, `visit_lit_byte_mut`, `visit_lit_byte_str_mut`, `visit_lit_cstr_mut`, `visit_lit_char_mut`, `visit_lit_float_mut`, `visit_lit_int_mut`, `visit_lit_str_mut`, `visit_local_mut`, `visit_local_init_mut`, `visit_macro_mut`, `visit_macro_delimiter_mut`, `visit_member_mut`, `visit_meta_mut`, `visit_meta_list_mut`, `visit_meta_name_value_mut`, `visit_parenthesized_generic_arguments_mut`, `visit_pat_mut`, `visit_pat_ident_mut`, `visit_pat_or_mut`, `visit_pat_paren_mut`, `visit_pat_reference_mut`, `visit_pat_rest_mut`, `visit_pat_slice_mut`, `visit_pat_struct_mut`, `visit_pat_tuple_mut`, `visit_pat_tuple_struct_mut`, `visit_pat_type_mut`, `visit_pat_wild_mut`, `visit_path_mut`, `visit_path_arguments_mut`, `visit_path_segment_mut`, `visit_pointer_mutability_mut`, `visit_precise_capture_mut`, `visit_predicate_lifetime_mut`, `visit_predicate_type_mut`, `visit_qself_mut`, `visit_range_limits_mut`, `visit_receiver_mut`, `visit_return_type_mut`, `visit_signature_mut`, `visit_span_mut`, `visit_static_mutability_mut`, `visit_stmt_mut`, `visit_stmt_macro_mut`, `visit_token_stream_mut`, `visit_trait_bound_mut`, `visit_trait_bound_modifier_mut`, `visit_trait_item_mut`, `visit_trait_item_const_mut`, `visit_trait_item_fn_mut`, `visit_trait_item_macro_mut`, `visit_trait_item_type_mut`, `visit_type_mut`, `visit_type_array_mut`, `visit_type_bare_fn_mut`, `visit_type_group_mut`, `visit_type_impl_trait_mut`, `visit_type_infer_mut`, `visit_type_macro_mut`, `visit_type_never_mut`, `visit_type_param_mut`, `visit_type_param_bound_mut`, `visit_type_paren_mut`, `visit_type_path_mut`, `visit_type_ptr_mut`, `visit_type_reference_mut`, `visit_type_slice_mut`, `visit_type_trait_object_mut`, `visit_type_tuple_mut`, `visit_un_op_mut`, `visit_use_glob_mut`, `visit_use_group_mut`, `visit_use_name_mut`, `visit_use_path_mut`, `visit_use_rename_mut`, `visit_use_tree_mut`, `visit_variadic_mut`, `visit_variant_mut`, `visit_vis_restricted_mut`, `visit_visibility_mut`, `visit_where_clause_mut`, `visit_where_predicate_mut`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 15. gen.visit

- **Target:** `gen.Visit`
- **Similarity:** 0.02
- **Dependents:** 1
- **Priority Score:** 2758909.8
- **Functions:** 14/188 matched (target 14)
- **Missing functions:** `visit_abi`, `visit_angle_bracketed_generic_arguments`, `visit_arm`, `visit_assoc_const`, `visit_assoc_type`, `visit_attr_style`, `visit_bare_fn_arg`, `visit_bare_variadic`, `visit_bin_op`, `visit_block`, `visit_bound_lifetimes`, `visit_captured_param`, `visit_const_param`, `visit_constraint`, `visit_data_enum`, `visit_data_struct`, `visit_data_union`, `visit_expr_array`, `visit_expr_assign`, `visit_expr_async`, `visit_expr_await`, `visit_expr_binary`, `visit_expr_block`, `visit_expr_break`, `visit_expr_call`, `visit_expr_cast`, `visit_expr_closure`, `visit_expr_const`, `visit_expr_continue`, `visit_expr_field`, `visit_expr_for_loop`, `visit_expr_group`, `visit_expr_if`, `visit_expr_index`, `visit_expr_infer`, `visit_expr_let`, `visit_expr_lit`, `visit_expr_loop`, `visit_expr_macro`, `visit_expr_match`, `visit_expr_method_call`, `visit_expr_paren`, `visit_expr_path`, `visit_expr_range`, `visit_expr_raw_addr`, `visit_expr_reference`, `visit_expr_repeat`, `visit_expr_return`, `visit_expr_struct`, `visit_expr_try`, `visit_expr_try_block`, `visit_expr_tuple`, `visit_expr_unary`, `visit_expr_unsafe`, `visit_expr_while`, `visit_expr_yield`, `visit_field`, `visit_field_mutability`, `visit_field_pat`, `visit_field_value`, `visit_fields`, `visit_fields_named`, `visit_fields_unnamed`, `visit_file`, `visit_fn_arg`, `visit_foreign_item`, `visit_foreign_item_fn`, `visit_foreign_item_macro`, `visit_foreign_item_static`, `visit_foreign_item_type`, `visit_generic_argument`, `visit_generic_param`, `visit_impl_item`, `visit_impl_item_const`, `visit_impl_item_fn`, `visit_impl_item_macro`, `visit_impl_item_type`, `visit_impl_restriction`, `visit_index`, `visit_item_const`, `visit_item_enum`, `visit_item_extern_crate`, `visit_item_fn`, `visit_item_foreign_mod`, `visit_item_impl`, `visit_item_macro`, `visit_item_mod`, `visit_item_static`, `visit_item_struct`, `visit_item_trait`, `visit_item_trait_alias`, `visit_item_type`, `visit_item_union`, `visit_item_use`, `visit_label`, `visit_lifetime_param`, `visit_lit_bool`, `visit_lit_byte`, `visit_lit_byte_str`, `visit_lit_cstr`, `visit_lit_char`, `visit_lit_float`, `visit_lit_int`, `visit_lit_str`, `visit_local`, `visit_local_init`, `visit_macro`, `visit_macro_delimiter`, `visit_member`, `visit_meta_list`, `visit_meta_name_value`, `visit_parenthesized_generic_arguments`, `visit_pat_ident`, `visit_pat_or`, `visit_pat_paren`, `visit_pat_reference`, `visit_pat_rest`, `visit_pat_slice`, `visit_pat_struct`, `visit_pat_tuple`, `visit_pat_tuple_struct`, `visit_pat_type`, `visit_pat_wild`, `visit_path_arguments`, `visit_path_segment`, `visit_pointer_mutability`, `visit_precise_capture`, `visit_predicate_lifetime`, `visit_predicate_type`, `visit_qself`, `visit_range_limits`, `visit_receiver`, `visit_return_type`, `visit_signature`, `visit_span`, `visit_static_mutability`, `visit_stmt_macro`, `visit_token_stream`, `visit_trait_bound`, `visit_trait_bound_modifier`, `visit_trait_item`, `visit_trait_item_const`, `visit_trait_item_fn`, `visit_trait_item_macro`, `visit_trait_item_type`, `visit_type_array`, `visit_type_bare_fn`, `visit_type_group`, `visit_type_impl_trait`, `visit_type_infer`, `visit_type_macro`, `visit_type_never`, `visit_type_param`, `visit_type_param_bound`, `visit_type_paren`, `visit_type_path`, `visit_type_ptr`, `visit_type_reference`, `visit_type_slice`, `visit_type_trait_object`, `visit_type_tuple`, `visit_un_op`, `visit_use_glob`, `visit_use_group`, `visit_use_name`, `visit_use_path`, `visit_use_rename`, `visit_use_tree`, `visit_variadic`, `visit_variant`, `visit_vis_restricted`, `visit_visibility`, `visit_where_clause`, `visit_where_predicate`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Lint issues:** 14

### 16. item

- **Target:** `syn.Item [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2253210.0
- **Functions:** 7/28 matched (target 84)
- **Missing functions:** `replace_attrs`, `from`, `parse_rest_of_item`, `parse_optional_bounds`, `parse_optional_definition`, `parse_macro2`, `parse_item_use`, `parse_rest_of_fn`, `parse_fn_arg_or_variadic`, `parse_fn_args`, `parse_foreign_item_type`, `parse_item_type`, `parse_trait_or_trait_alias`, `parse_rest_of_trait`, `parse_start_of_trait_alias`, `parse_rest_of_trait_alias`, `parse_trait_item_type`, `parse_impl`, `parse_impl_item_fn`, `parse_impl_item_type`, `is_inherited`
- **Types:** 0/4 matched (target 47)
- **Missing types:** `FlexibleItemType`, `TypeDefaultness`, `WhereClauseLocation`, `FnArgOrVariadic`

### 17. pat

- **Target:** `syn.Pat`
- **Similarity:** 0.30
- **Dependents:** 2
- **Priority Score:** 2232407.0
- **Functions:** 1/23 matched (target 40)
- **Missing functions:** `parse_single`, `parse_multi`, `parse_multi_with_leading_vert`, `parse`, `multi_pat_impl`, `pat_path_or_macro_or_struct_or_range`, `pat_wild`, `pat_box`, `pat_ident`, `pat_tuple_struct`, `pat_struct`, `field_pat`, `pat_range`, `pat_range_half_open`, `pat_paren_or_tuple`, `pat_reference`, `pat_lit_or_range`, `into_expr`, `into_pat`, `pat_range_bound`, `pat_slice`, `pat_const`
- **Types:** 0/1 matched (target 21)
- **Missing types:** `PatRangeBound`

### 18. generics

- **Target:** `syn.Generics`
- **Similarity:** 0.28
- **Dependents:** 2
- **Priority Score:** 2192507.2
- **Functions:** 5/15 matched (target 40)
- **Missing functions:** `next`, `as_turbofish`, `from`, `parse`, `parse_single`, `parse_multiple`, `do_parse`, `choose_generics_over_qpath`, `choose_generics_over_qpath_after_keyword`, `print_const_argument`
- **Types:** 1/10 matched (target 23)
- **Missing types:** `Lifetimes`, `Item`, `LifetimesMut`, `TypeParams`, `TypeParamsMut`, `ConstParams`, `ConstParamsMut`, `ImplGenerics`, `TypeGenerics`
- **Lint issues:** 1

### 19. precedence

- **Target:** `syn.Precedence`
- **Similarity:** 0.66
- **Dependents:** 2
- **Priority Score:** 2010703.4
- **Functions:** 5/6 matched
- **Missing functions:** `clone`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 20. attr

- **Target:** `syn.Attr`
- **Similarity:** 0.32
- **Dependents:** 1
- **Priority Score:** 1202706.9
- **Functions:** 7/23 matched (target 29)
- **Missing functions:** `parse_args`, `parse_args_with`, `parse_outer`, `parse_inner`, `require_path_only`, `require_list`, `require_name_value`, `outer`, `is_outer`, `inner`, `is_inner`, `from`, `single_parse_inner`, `single_parse_outer`, `parse_outermost_meta_path`, `fmt`
- **Types:** 0/4 matched (target 13)
- **Missing types:** `FilterAttrs`, `Ret`, `DisplayAttrStyle`, `DisplayPath`
- **Lint issues:** 1

### 21. ty

- **Target:** `syn.Type`
- **Similarity:** 0.32
- **Dependents:** 1
- **Priority Score:** 1060706.8
- **Functions:** 1/7 matched (target 41)
- **Missing functions:** `parse`, `without_plus`, `ambig_ty`, `parse_bounds`, `parse_bare_fn_arg`, `parse_bare_variadic`
- **Types:** 0/0 matched (target 24)
- **Missing types:** _none_
- **Lint issues:** 1

### 22. derive

- **Target:** `syn.Derive`
- **Similarity:** 0.11
- **Dependents:** 1
- **Priority Score:** 1040508.9
- **Functions:** 1/5 matched (target 2)
- **Missing functions:** `data_struct`, `data_enum`, `data_union`, `to_tokens`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 23. scan_expr

- **Target:** `syn.ScanExpr`
- **Similarity:** 0.58
- **Dependents:** 1
- **Priority Score:** 1020304.2
- **Functions:** 1/1 matched (target 78)
- **Missing functions:** _none_
- **Types:** 0/2 matched (target 4)
- **Missing types:** `Input`, `Action`
- **Lint issues:** 1

### 24. gen.debug

- **Target:** `gen.Debug [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1020210.0
- **Functions:** 0/2 matched (target 0)
- **Missing functions:** `fmt`, `debug`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 25. group

- **Target:** `syn.Group`
- **Similarity:** 0.88
- **Dependents:** 1
- **Priority Score:** 1010901.2
- **Functions:** 5/5 matched (target 8)
- **Missing functions:** _none_
- **Types:** 3/4 matched
- **Missing types:** `Group`

### 26. spanned

- **Target:** `syn.Spanned`
- **Similarity:** 0.97
- **Dependents:** 1
- **Priority Score:** 1010300.3
- **Functions:** 1/1 matched (target 3)
- **Missing functions:** _none_
- **Types:** 1/2 matched
- **Missing types:** `Sealed`

### 27. gen.clone

- **Target:** `gen.Clone [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1010110.0
- **Functions:** 0/1 matched (target 0)
- **Missing functions:** `clone`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 28. bigint

- **Target:** `syn.BigInt`
- **Similarity:** 0.67
- **Dependents:** 1
- **Priority Score:** 1000603.3
- **Functions:** 5/5 matched (target 8)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 29. file

- **Target:** `syn.File`
- **Similarity:** 0.69
- **Dependents:** 1
- **Priority Score:** 1000203.1
- **Functions:** 2/2 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
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

### 31. fixup

- **Target:** `syn.Fixup`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 161710.0
- **Functions:** 0/15 matched (target 1)
- **Missing functions:** `new_stmt`, `new_match_arm`, `new_condition`, `leftmost_subexpression_with_operator`, `leftmost_subexpression_with_dot`, `leftmost_subexpression_precedence`, `rightmost_subexpression`, `rightmost_subexpression_fixup`, `rightmost_subexpression_precedence`, `parenthesize`, `precedence`, `clone`, `eq`, `scan_left`, `scan_right`
- **Types:** 1/2 matched
- **Missing types:** `Scan`

### 32. data

- **Target:** `syn.Data`
- **Similarity:** 0.25
- **Dependents:** 0
- **Priority Score:** 111407.5
- **Functions:** 3/11 matched (target 19)
- **Missing functions:** `iter`, `iter_mut`, `into_iter`, `next`, `clone`, `parse`, `parse_named`, `parse_unnamed`
- **Types:** 0/3 matched (target 16)
- **Missing types:** `Item`, `IntoIter`, `Members`
- **Lint issues:** 1

### 33. ext

- **Target:** `syn.Ext`
- **Similarity:** 0.39
- **Dependents:** 0
- **Priority Score:** 111406.1
- **Functions:** 3/7 matched (target 6)
- **Missing functions:** `parse_any`, `append`, `new_spanned`, `clone`
- **Types:** 0/7 matched (target 1)
- **Missing types:** `IdentExt`, `Token`, `TokenStreamExt`, `PunctExt`, `Sealed`, `PeekFn`, `IdentAny`

### 34. parse

- **Target:** `syn.Parse`
- **Similarity:** 0.46
- **Dependents:** 0
- **Priority Score:** 104205.4
- **Functions:** 25/33 matched (target 55)
- **Missing functions:** `drop`, `fmt`, `deref`, `default`, `cell_clone`, `to_tokens`, `eq`, `hash`
- **Types:** 7/9 matched (target 16)
- **Missing types:** `Target`, `Output`
- **Lint issues:** 1

### 35. export

- **Target:** `syn.Export`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 80800.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/8 matched (target 1)
- **Missing types:** `Formatter`, `FmtResult`, `bool`, `str`, `Span`, `TokenStream2`, `TokenStream`, `private`

### 36. buffer

- **Target:** `syn.Buffer`
- **Similarity:** 0.63
- **Dependents:** 0
- **Priority Score:** 53503.7
- **Functions:** 27/31 matched (target 29)
- **Missing functions:** `new`, `clone`, `eq`, `partial_cmp`
- **Types:** 3/4 matched (target 9)
- **Missing types:** `UnsafeSyncEntry`

### 37. meta

- **Target:** `syn.Meta`
- **Similarity:** 0.10
- **Dependents:** 0
- **Priority Score:** 50609.0
- **Functions:** 1/5 matched (target 3)
- **Missing functions:** `parser`, `value`, `parse_nested_meta`, `error`
- **Types:** 0/1 matched (target 0)
- **Missing types:** `ParseNestedMeta`

### 38. drops

- **Target:** `syn.Drops`
- **Similarity:** 0.47
- **Dependents:** 0
- **Priority Score:** 30905.3
- **Functions:** 4/5 matched (target 10)
- **Missing functions:** `test_needs_drop`
- **Types:** 2/4 matched (target 2)
- **Missing types:** `Target`, `NeedsDrop`
- **Tests:** 0/1 matched

### 39. thread

- **Target:** `syn.Thread`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 20507.0
- **Functions:** 2/4 matched (target 11)
- **Missing functions:** `fmt`, `clone`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 40. tt

- **Target:** `syn.Tt`
- **Similarity:** 0.18
- **Dependents:** 0
- **Priority Score:** 20408.2
- **Functions:** 2/2 matched (target 7)
- **Missing functions:** _none_
- **Types:** 0/2 matched (target 0)
- **Missing types:** `TokenTreeHelper`, `TokenStreamHelper`

### 41. stmt

- **Target:** `syn.Stmt`
- **Similarity:** 0.64
- **Dependents:** 0
- **Priority Score:** 10803.6
- **Functions:** 7/7 matched (target 19)
- **Missing functions:** _none_
- **Types:** 0/1 matched (target 8)
- **Missing types:** `AllowNoSemi`

### 42. mac

- **Target:** `syn.Mac`
- **Similarity:** 0.65
- **Dependents:** 0
- **Priority Score:** 10803.5
- **Functions:** 7/8 matched
- **Missing functions:** `parse`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 43. gen.eq

- **Target:** `gen.Eq [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10110.0
- **Functions:** 0/1 matched (target 0)
- **Missing functions:** `eq`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 44. gen.hash

- **Target:** `gen.Hash [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10110.0
- **Functions:** 0/1 matched (target 0)
- **Missing functions:** `hash`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 45. restriction

- **Target:** `syn.Restriction`
- **Similarity:** 0.64
- **Dependents:** 0
- **Priority Score:** 403.6
- **Functions:** 4/4 matched (target 14)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 12)
- **Missing types:** _none_
- **Lint issues:** 2

### 46. discouraged

- **Target:** `syn.Discouraged`
- **Similarity:** 0.71
- **Dependents:** 0
- **Priority Score:** 402.9
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_

### 47. parse_quote

- **Target:** `syn.ParseQuote`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 204.9
- **Functions:** 1/1 matched (target 8)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 6)
- **Missing types:** _none_

### 48. whitespace

- **Target:** `syn.Whitespace`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 204.9
- **Functions:** 2/2 matched (target 6)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 49. op

- **Target:** `syn.Op`
- **Similarity:** 0.68
- **Dependents:** 0
- **Priority Score:** 203.2
- **Functions:** 2/2 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 35)
- **Missing types:** _none_

### 50. print

- **Target:** `syn.Print`
- **Similarity:** 0.86
- **Dependents:** 0
- **Priority Score:** 201.4
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
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

