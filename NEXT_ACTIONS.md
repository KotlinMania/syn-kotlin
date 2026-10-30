# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 52/55 (94.5%)
- **Function parity:** 1646/2927 matched (target 3440) — 56.2%
- **Class/type parity:** 96/121 matched (target 911) — 79.3%
- **Combined symbol parity:** 1742/3048 matched (target 4351) — 57.2%
- **Average inline-code cosine:** 0.11 (function body across 52 matched files)
- **Average documentation cosine:** 0.33 (doc text across 52 matched files)
- **Cheat-zeroed Files:** 20
- **Critical Issues:** 51 files with <0.60 function similarity
- **Needs Review:** 0 files with 0.60-0.84 function similarity
- **Excellent:** 1 files with >=0.85 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. token
- **Similarity:** 0.00 (needs 85% improvement)
- **Dependencies:** 17
- **Priority Score:** 17023410.0
- **Functions:** 27/29 matched (target 685)
- **Missing functions:** `punct`, `keyword`
- **Types:** 5/5 matched (target 305)
- **Missing types:** _none_
- **Symbol Deficit:** 2 (functions: 2, types: 0)
- **Action:** Deep review - likely missing major functionality

### 2. ident
- **Similarity:** 0.00 (needs 85% improvement)
- **Dependencies:** 14
- **Priority Score:** 14010610.0
- **Functions:** 5/6 matched (target 63)
- **Missing functions:** `accept_as_ident`
- **Types:** 0/0 matched (target 30)
- **Missing types:** _none_
- **Symbol Deficit:** 1 (functions: 1, types: 0)
- **Action:** Deep review - likely missing major functionality

### 3. punctuated
- **Similarity:** 0.10 (needs 75% improvement)
- **Dependencies:** 13
- **Priority Score:** 13030709.0
- **Functions:** 91/93 matched (target 348)
- **Missing functions:** `into_iter`, `into_iter`
- **Types:** 14/14 matched (target 36)
- **Missing types:** _none_
- **Symbol Deficit:** 2 (functions: 2, types: 0)
- **Action:** Deep review - likely missing major functionality

### 4. expr
- **Similarity:** 0.00 (needs 85% improvement)
- **Dependencies:** 10
- **Priority Score:** 10455910.0
- **Functions:** 114/158 matched (target 179)
- **Missing functions:** `from`, `eq`, `hash`, `fmt`, `span`, `parse_with_earlier_boundary_rule`, `parse_expr`, `unary_expr`, `trailer_expr`, `atom_expr`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `outer_attrs_to_tokens`
- **Types:** 1/1 matched (target 56)
- **Missing types:** _none_
- **Symbol Deficit:** 44 (functions: 44, types: 0)
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
- **Priority Score:** 17023410.0
- **Functions:** 27/29 matched (target 685)
- **Missing functions:** `punct`, `keyword`
- **Types:** 5/5 matched (target 305)
- **Missing types:** _none_

### 2. ident

- **Target:** `syn.Ident [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 14
- **Priority Score:** 14010610.0
- **Functions:** 5/6 matched (target 63)
- **Missing functions:** `accept_as_ident`
- **Types:** 0/0 matched (target 30)
- **Missing types:** _none_

### 3. punctuated

- **Target:** `syn.Punctuated`
- **Similarity:** 0.10
- **Dependents:** 13
- **Priority Score:** 13030709.0
- **Functions:** 91/93 matched (target 348)
- **Missing functions:** `into_iter`, `into_iter`
- **Types:** 14/14 matched (target 36)
- **Missing types:** _none_

### 4. expr

- **Target:** `syn.Expr [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 10
- **Priority Score:** 10455910.0
- **Functions:** 114/158 matched (target 179)
- **Missing functions:** `from`, `eq`, `hash`, `fmt`, `span`, `parse_with_earlier_boundary_rule`, `parse_expr`, `unary_expr`, `trailer_expr`, `atom_expr`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `outer_attrs_to_tokens`
- **Types:** 1/1 matched (target 56)
- **Missing types:** _none_

### 5. path

- **Target:** `syn.Path`
- **Similarity:** 0.33
- **Dependents:** 9
- **Priority Score:** 9014307.0
- **Functions:** 40/41 matched (target 67)
- **Missing functions:** `to_tokens`
- **Types:** 2/2 matched (target 28)
- **Missing types:** _none_
- **Lint issues:** 1

### 6. lifetime

- **Target:** `syn.Lifetime`
- **Similarity:** 0.23
- **Dependents:** 8
- **Priority Score:** 8001207.5
- **Functions:** 11/11 matched (target 18)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 3)
- **Missing types:** _none_

### 7. verbatim

- **Target:** `syn.Verbatim [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 7
- **Priority Score:** 7010110.0
- **Functions:** 0/1 matched (target 4)
- **Missing functions:** `between`
- **Types:** 0/0 matched (target 5)
- **Missing types:** _none_

### 8. lit

- **Target:** `syn.Lit [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 5
- **Priority Score:** 5152210.0
- **Functions:** 104/118 matched (target 160)
- **Missing functions:** `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `parse`, `parse`, `parse`, `parse`, `suffix`
- **Types:** 4/4 matched (target 36)
- **Missing types:** _none_

### 9. lookahead

- **Target:** `syn.Lookahead [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 5
- **Priority Score:** 5001510.0
- **Functions:** 9/9 matched (target 24)
- **Missing functions:** _none_
- **Types:** 6/6 matched (target 13)
- **Missing types:** _none_

### 10. span

- **Target:** `syn.Span [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 4
- **Priority Score:** 4101010.0
- **Functions:** 0/9 matched (target 12)
- **Missing functions:** `into_spans`, `into_spans`, `into_spans`, `into_spans`, `into_spans`, `into_spans`, `into_spans`, `into_spans`, `into_spans`
- **Types:** 0/1 matched (target 13)
- **Missing types:** `IntoSpans`

### 11. gen.debug

- **Target:** `gen.Debug`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 3598210.0
- **Functions:** 25/282 matched (target 28)
- **Missing functions:** `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `fmt`, `fmt`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `fmt`, `fmt`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `fmt`, `debug`, `fmt`, `fmt`, `fmt`, `debug`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `debug`, `fmt`, `fmt`, `fmt`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `fmt`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `debug`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `fmt`, `debug`, `fmt`, `fmt`, `fmt`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 12. classify

- **Target:** `syn.Classify [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 3
- **Priority Score:** 3111110.0
- **Functions:** 0/11 matched (target 13)
- **Missing functions:** `requires_semi_to_be_stmt`, `requires_comma_to_be_match_arm`, `trailing_unparameterized_path`, `last_type_in_path`, `last_type_in_bounds`, `expr_leading_label`, `expr_trailing_brace`, `type_trailing_brace`, `last_type_in_path`, `last_type_in_bounds`, `tokens_trailing_brace`
- **Types:** 0/0 matched (target 7)
- **Missing types:** _none_

### 13. error

- **Target:** `syn.Error`
- **Similarity:** 0.18
- **Dependents:** 3
- **Priority Score:** 3083208.2
- **Functions:** 18/24 matched (target 41)
- **Missing functions:** `new`, `new2`, `fmt`, `clone`, `clone`, `into_iter`
- **Types:** 6/8 matched (target 11)
- **Missing types:** `Result`, `Error`

### 14. gen.fold

- **Target:** `gen.Fold`
- **Similarity:** 0.05
- **Dependents:** 1
- **Priority Score:** 2877809.5
- **Functions:** 193/377 matched (target 197)
- **Missing functions:** `fold_abi`, `fold_angle_bracketed_generic_arguments`, `fold_arm`, `fold_assoc_const`, `fold_assoc_type`, `fold_attr_style`, `fold_attribute`, `fold_bare_fn_arg`, `fold_bare_variadic`, `fold_bin_op`, `fold_block`, `fold_bound_lifetimes`, `fold_captured_param`, `fold_const_param`, `fold_constraint`, `fold_data`, `fold_data_enum`, `fold_data_struct`, `fold_data_union`, `fold_derive_input`, `fold_expr`, `fold_expr_array`, `fold_expr_assign`, `fold_expr_async`, `fold_expr_await`, `fold_expr_binary`, `fold_expr_block`, `fold_expr_break`, `fold_expr_call`, `fold_expr_cast`, `fold_expr_closure`, `fold_expr_const`, `fold_expr_continue`, `fold_expr_field`, `fold_expr_for_loop`, `fold_expr_group`, `fold_expr_if`, `fold_expr_index`, `fold_expr_infer`, `fold_expr_let`, `fold_expr_lit`, `fold_expr_loop`, `fold_expr_macro`, `fold_expr_match`, `fold_expr_method_call`, `fold_expr_paren`, `fold_expr_path`, `fold_expr_range`, `fold_expr_raw_addr`, `fold_expr_reference`, `fold_expr_repeat`, `fold_expr_return`, `fold_expr_struct`, `fold_expr_try`, `fold_expr_try_block`, `fold_expr_tuple`, `fold_expr_unary`, `fold_expr_unsafe`, `fold_expr_while`, `fold_expr_yield`, `fold_field`, `fold_field_mutability`, `fold_field_pat`, `fold_field_value`, `fold_fields`, `fold_fields_named`, `fold_fields_unnamed`, `fold_file`, `fold_fn_arg`, `fold_foreign_item`, `fold_foreign_item_fn`, `fold_foreign_item_macro`, `fold_foreign_item_static`, `fold_foreign_item_type`, `fold_generic_argument`, `fold_generic_param`, `fold_generics`, `fold_ident`, `fold_impl_item`, `fold_impl_item_const`, `fold_impl_item_fn`, `fold_impl_item_macro`, `fold_impl_item_type`, `fold_impl_restriction`, `fold_index`, `fold_item`, `fold_item_const`, `fold_item_enum`, `fold_item_extern_crate`, `fold_item_fn`, `fold_item_foreign_mod`, `fold_item_impl`, `fold_item_macro`, `fold_item_mod`, `fold_item_static`, `fold_item_struct`, `fold_item_trait`, `fold_item_trait_alias`, `fold_item_type`, `fold_item_union`, `fold_item_use`, `fold_label`, `fold_lifetime`, `fold_lifetime_param`, `fold_lit`, `fold_lit_bool`, `fold_lit_byte`, `fold_lit_byte_str`, `fold_lit_char`, `fold_lit_float`, `fold_lit_int`, `fold_lit_str`, `fold_local`, `fold_local_init`, `fold_macro`, `fold_macro_delimiter`, `fold_member`, `fold_meta`, `fold_meta_list`, `fold_meta_name_value`, `fold_parenthesized_generic_arguments`, `fold_pat`, `fold_pat_ident`, `fold_pat_or`, `fold_pat_paren`, `fold_pat_reference`, `fold_pat_slice`, `fold_pat_struct`, `fold_pat_tuple`, `fold_pat_tuple_struct`, `fold_pat_type`, `fold_pat_wild`, `fold_path`, `fold_path_arguments`, `fold_path_segment`, `fold_pointer_mutability`, `fold_precise_capture`, `fold_predicate_lifetime`, `fold_predicate_type`, `fold_range_limits`, `fold_receiver`, `fold_return_type`, `fold_signature`, `fold_span`, `fold_static_mutability`, `fold_stmt`, `fold_stmt_macro`, `fold_trait_bound`, `fold_trait_bound_modifier`, `fold_trait_item`, `fold_trait_item_const`, `fold_trait_item_fn`, `fold_trait_item_macro`, `fold_trait_item_type`, `fold_type`, `fold_type_array`, `fold_type_bare_fn`, `fold_type_group`, `fold_type_impl_trait`, `fold_type_infer`, `fold_type_macro`, `fold_type_never`, `fold_type_param`, `fold_type_param_bound`, `fold_type_paren`, `fold_type_path`, `fold_type_ptr`, `fold_type_reference`, `fold_type_slice`, `fold_type_trait_object`, `fold_type_tuple`, `fold_un_op`, `fold_use_glob`, `fold_use_group`, `fold_use_name`, `fold_use_path`, `fold_use_rename`, `fold_use_tree`, `fold_variadic`, `fold_variant`, `fold_vis_restricted`, `fold_visibility`, `fold_where_clause`, `fold_where_predicate`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 15. gen.visit

- **Target:** `gen.Visit`
- **Similarity:** 0.05
- **Dependents:** 1
- **Priority Score:** 2867609.5
- **Functions:** 192/375 matched (target 201)
- **Missing functions:** `visit_abi`, `visit_angle_bracketed_generic_arguments`, `visit_arm`, `visit_assoc_const`, `visit_assoc_type`, `visit_attr_style`, `visit_attribute`, `visit_bare_fn_arg`, `visit_bare_variadic`, `visit_bin_op`, `visit_block`, `visit_bound_lifetimes`, `visit_captured_param`, `visit_const_param`, `visit_constraint`, `visit_data`, `visit_data_enum`, `visit_data_struct`, `visit_data_union`, `visit_derive_input`, `visit_expr`, `visit_expr_array`, `visit_expr_assign`, `visit_expr_async`, `visit_expr_await`, `visit_expr_binary`, `visit_expr_block`, `visit_expr_break`, `visit_expr_call`, `visit_expr_cast`, `visit_expr_closure`, `visit_expr_const`, `visit_expr_continue`, `visit_expr_field`, `visit_expr_for_loop`, `visit_expr_group`, `visit_expr_if`, `visit_expr_index`, `visit_expr_infer`, `visit_expr_let`, `visit_expr_lit`, `visit_expr_loop`, `visit_expr_macro`, `visit_expr_match`, `visit_expr_method_call`, `visit_expr_paren`, `visit_expr_path`, `visit_expr_range`, `visit_expr_raw_addr`, `visit_expr_reference`, `visit_expr_repeat`, `visit_expr_return`, `visit_expr_struct`, `visit_expr_try`, `visit_expr_try_block`, `visit_expr_tuple`, `visit_expr_unary`, `visit_expr_unsafe`, `visit_expr_while`, `visit_expr_yield`, `visit_field`, `visit_field_mutability`, `visit_field_pat`, `visit_field_value`, `visit_fields`, `visit_fields_named`, `visit_fields_unnamed`, `visit_file`, `visit_fn_arg`, `visit_foreign_item`, `visit_foreign_item_fn`, `visit_foreign_item_macro`, `visit_foreign_item_static`, `visit_foreign_item_type`, `visit_generic_argument`, `visit_generic_param`, `visit_generics`, `visit_ident`, `visit_impl_item`, `visit_impl_item_const`, `visit_impl_item_fn`, `visit_impl_item_macro`, `visit_impl_item_type`, `visit_impl_restriction`, `visit_index`, `visit_item`, `visit_item_const`, `visit_item_enum`, `visit_item_extern_crate`, `visit_item_fn`, `visit_item_foreign_mod`, `visit_item_impl`, `visit_item_macro`, `visit_item_mod`, `visit_item_static`, `visit_item_struct`, `visit_item_trait`, `visit_item_trait_alias`, `visit_item_type`, `visit_item_union`, `visit_item_use`, `visit_label`, `visit_lifetime`, `visit_lifetime_param`, `visit_lit`, `visit_lit_bool`, `visit_lit_byte`, `visit_lit_byte_str`, `visit_lit_char`, `visit_lit_float`, `visit_lit_int`, `visit_lit_str`, `visit_local`, `visit_local_init`, `visit_macro`, `visit_macro_delimiter`, `visit_member`, `visit_meta`, `visit_meta_list`, `visit_meta_name_value`, `visit_parenthesized_generic_arguments`, `visit_pat`, `visit_pat_ident`, `visit_pat_or`, `visit_pat_paren`, `visit_pat_reference`, `visit_pat_slice`, `visit_pat_struct`, `visit_pat_tuple`, `visit_pat_tuple_struct`, `visit_pat_type`, `visit_pat_wild`, `visit_path`, `visit_path_arguments`, `visit_path_segment`, `visit_precise_capture`, `visit_predicate_lifetime`, `visit_predicate_type`, `visit_range_limits`, `visit_receiver`, `visit_return_type`, `visit_signature`, `visit_span`, `visit_static_mutability`, `visit_stmt`, `visit_stmt_macro`, `visit_trait_bound`, `visit_trait_bound_modifier`, `visit_trait_item`, `visit_trait_item_const`, `visit_trait_item_fn`, `visit_trait_item_macro`, `visit_trait_item_type`, `visit_type`, `visit_type_array`, `visit_type_bare_fn`, `visit_type_group`, `visit_type_impl_trait`, `visit_type_infer`, `visit_type_macro`, `visit_type_never`, `visit_type_param`, `visit_type_param_bound`, `visit_type_paren`, `visit_type_path`, `visit_type_ptr`, `visit_type_reference`, `visit_type_slice`, `visit_type_trait_object`, `visit_type_tuple`, `visit_un_op`, `visit_use_glob`, `visit_use_group`, `visit_use_name`, `visit_use_path`, `visit_use_rename`, `visit_use_tree`, `visit_variadic`, `visit_variant`, `visit_vis_restricted`, `visit_visibility`, `visit_where_clause`, `visit_where_predicate`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Lint issues:** 15

### 16. gen.visit_mut

- **Target:** `gen.VisitMut`
- **Similarity:** 0.05
- **Dependents:** 1
- **Priority Score:** 2857709.5
- **Functions:** 194/376 matched (target 318)
- **Missing functions:** `visit_abi_mut`, `visit_angle_bracketed_generic_arguments_mut`, `visit_arm_mut`, `visit_assoc_const_mut`, `visit_assoc_type_mut`, `visit_attr_style_mut`, `visit_attribute_mut`, `visit_bare_fn_arg_mut`, `visit_bare_variadic_mut`, `visit_bin_op_mut`, `visit_block_mut`, `visit_bound_lifetimes_mut`, `visit_captured_param_mut`, `visit_const_param_mut`, `visit_constraint_mut`, `visit_data_mut`, `visit_data_enum_mut`, `visit_data_struct_mut`, `visit_data_union_mut`, `visit_derive_input_mut`, `visit_expr_mut`, `visit_expr_array_mut`, `visit_expr_assign_mut`, `visit_expr_async_mut`, `visit_expr_await_mut`, `visit_expr_binary_mut`, `visit_expr_block_mut`, `visit_expr_break_mut`, `visit_expr_call_mut`, `visit_expr_cast_mut`, `visit_expr_closure_mut`, `visit_expr_const_mut`, `visit_expr_continue_mut`, `visit_expr_field_mut`, `visit_expr_for_loop_mut`, `visit_expr_group_mut`, `visit_expr_if_mut`, `visit_expr_index_mut`, `visit_expr_infer_mut`, `visit_expr_let_mut`, `visit_expr_lit_mut`, `visit_expr_loop_mut`, `visit_expr_macro_mut`, `visit_expr_match_mut`, `visit_expr_method_call_mut`, `visit_expr_paren_mut`, `visit_expr_path_mut`, `visit_expr_range_mut`, `visit_expr_raw_addr_mut`, `visit_expr_reference_mut`, `visit_expr_repeat_mut`, `visit_expr_return_mut`, `visit_expr_struct_mut`, `visit_expr_try_mut`, `visit_expr_try_block_mut`, `visit_expr_tuple_mut`, `visit_expr_unary_mut`, `visit_expr_unsafe_mut`, `visit_expr_while_mut`, `visit_expr_yield_mut`, `visit_field_mut`, `visit_field_mutability_mut`, `visit_field_pat_mut`, `visit_field_value_mut`, `visit_fields_mut`, `visit_fields_named_mut`, `visit_fields_unnamed_mut`, `visit_file_mut`, `visit_fn_arg_mut`, `visit_foreign_item_mut`, `visit_foreign_item_fn_mut`, `visit_foreign_item_macro_mut`, `visit_foreign_item_static_mut`, `visit_foreign_item_type_mut`, `visit_generic_argument_mut`, `visit_generic_param_mut`, `visit_generics_mut`, `visit_ident_mut`, `visit_impl_item_mut`, `visit_impl_item_const_mut`, `visit_impl_item_fn_mut`, `visit_impl_item_macro_mut`, `visit_impl_item_type_mut`, `visit_impl_restriction_mut`, `visit_index_mut`, `visit_item_mut`, `visit_item_const_mut`, `visit_item_enum_mut`, `visit_item_extern_crate_mut`, `visit_item_fn_mut`, `visit_item_foreign_mod_mut`, `visit_item_impl_mut`, `visit_item_macro_mut`, `visit_item_mod_mut`, `visit_item_static_mut`, `visit_item_struct_mut`, `visit_item_trait_mut`, `visit_item_trait_alias_mut`, `visit_item_type_mut`, `visit_item_union_mut`, `visit_item_use_mut`, `visit_label_mut`, `visit_lifetime_mut`, `visit_lifetime_param_mut`, `visit_lit_mut`, `visit_lit_bool_mut`, `visit_lit_byte_mut`, `visit_lit_byte_str_mut`, `visit_lit_char_mut`, `visit_lit_float_mut`, `visit_lit_int_mut`, `visit_lit_str_mut`, `visit_local_mut`, `visit_local_init_mut`, `visit_macro_mut`, `visit_macro_delimiter_mut`, `visit_member_mut`, `visit_meta_mut`, `visit_meta_list_mut`, `visit_meta_name_value_mut`, `visit_parenthesized_generic_arguments_mut`, `visit_pat_mut`, `visit_pat_ident_mut`, `visit_pat_or_mut`, `visit_pat_paren_mut`, `visit_pat_reference_mut`, `visit_pat_slice_mut`, `visit_pat_struct_mut`, `visit_pat_tuple_mut`, `visit_pat_tuple_struct_mut`, `visit_pat_wild_mut`, `visit_path_mut`, `visit_path_arguments_mut`, `visit_path_segment_mut`, `visit_precise_capture_mut`, `visit_predicate_lifetime_mut`, `visit_predicate_type_mut`, `visit_range_limits_mut`, `visit_receiver_mut`, `visit_return_type_mut`, `visit_signature_mut`, `visit_span_mut`, `visit_static_mutability_mut`, `visit_stmt_mut`, `visit_stmt_macro_mut`, `visit_trait_bound_mut`, `visit_trait_bound_modifier_mut`, `visit_trait_item_mut`, `visit_trait_item_const_mut`, `visit_trait_item_fn_mut`, `visit_trait_item_macro_mut`, `visit_trait_item_type_mut`, `visit_type_mut`, `visit_type_array_mut`, `visit_type_bare_fn_mut`, `visit_type_group_mut`, `visit_type_impl_trait_mut`, `visit_type_infer_mut`, `visit_type_macro_mut`, `visit_type_never_mut`, `visit_type_param_mut`, `visit_type_param_bound_mut`, `visit_type_paren_mut`, `visit_type_path_mut`, `visit_type_ptr_mut`, `visit_type_reference_mut`, `visit_type_slice_mut`, `visit_type_trait_object_mut`, `visit_type_tuple_mut`, `visit_un_op_mut`, `visit_use_glob_mut`, `visit_use_group_mut`, `visit_use_name_mut`, `visit_use_path_mut`, `visit_use_rename_mut`, `visit_use_tree_mut`, `visit_variadic_mut`, `visit_variant_mut`, `visit_vis_restricted_mut`, `visit_visibility_mut`, `visit_where_clause_mut`, `visit_where_predicate_mut`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Lint issues:** 24

### 17. item

- **Target:** `syn.Item [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2280610.0
- **Functions:** 75/102 matched (target 132)
- **Missing functions:** `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`
- **Types:** 4/4 matched (target 65)
- **Missing types:** _none_
- **Lint issues:** 1

### 18. precedence

- **Target:** `syn.Precedence [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2070710.0
- **Functions:** 0/6 matched (target 8)
- **Missing functions:** `of_binop`, `of`, `prefix_attrs`, `clone`, `eq`, `partial_cmp`
- **Types:** 0/1 matched (target 5)
- **Missing types:** `Precedence`

### 19. generics

- **Target:** `syn.Generics`
- **Similarity:** 0.20
- **Dependents:** 2
- **Priority Score:** 2025908.0
- **Functions:** 47/49 matched (target 93)
- **Missing functions:** `parse`, `parse`
- **Types:** 10/10 matched (target 34)
- **Missing types:** _none_
- **Lint issues:** 1

### 20. pat

- **Target:** `syn.Pat`
- **Similarity:** 0.25
- **Dependents:** 2
- **Priority Score:** 2003507.5
- **Functions:** 34/34 matched (target 65)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 22)
- **Missing types:** _none_

### 21. gen.eq

- **Target:** `gen.Eq`
- **Similarity:** 0.05
- **Dependents:** 0
- **Priority Score:** 1567509.5
- **Functions:** 20/175 matched (target 20)
- **Missing functions:** `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`, `eq`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 22. gen.hash

- **Target:** `gen.Hash`
- **Similarity:** 0.03
- **Dependents:** 0
- **Priority Score:** 1367509.6
- **Functions:** 40/175 matched (target 45)
- **Missing functions:** `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`, `hash`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 23. ty

- **Target:** `syn.Type`
- **Similarity:** 0.22
- **Dependents:** 1
- **Priority Score:** 1184907.9
- **Functions:** 31/49 matched (target 56)
- **Missing functions:** `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`
- **Types:** 0/0 matched (target 24)
- **Missing types:** _none_
- **Lint issues:** 1

### 24. bigint

- **Target:** `syn.BigInt [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1060610.0
- **Functions:** 0/5 matched
- **Missing functions:** `new`, `to_string`, `reserve_two_digits`, `add_assign`, `mul_assign`
- **Types:** 0/1 matched
- **Missing types:** `BigInt`

### 25. gen.clone

- **Target:** `gen.Clone`
- **Similarity:** 0.18
- **Dependents:** 1
- **Priority Score:** 1017708.2
- **Functions:** 177/177 matched (target 189)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 26. attr

- **Target:** `syn.Attr`
- **Similarity:** 0.18
- **Dependents:** 1
- **Priority Score:** 1014008.2
- **Functions:** 35/36 matched (target 49)
- **Missing functions:** `fmt`
- **Types:** 4/4 matched (target 17)
- **Missing types:** _none_
- **Lint issues:** 1

### 27. spanned

- **Target:** `syn.Spanned`
- **Similarity:** 0.09
- **Dependents:** 1
- **Priority Score:** 1010309.1
- **Functions:** 1/1 matched (target 4)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Sealed`

### 28. sealed

- **Target:** `syn.Sealed [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1010110.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/1 matched
- **Missing types:** `Sealed`

### 29. group

- **Target:** `syn.Group`
- **Similarity:** 0.47
- **Dependents:** 1
- **Priority Score:** 1000905.3
- **Functions:** 5/5 matched (target 8)
- **Missing functions:** _none_
- **Types:** 4/4 matched
- **Missing types:** _none_

### 30. derive

- **Target:** `syn.Derive`
- **Similarity:** 0.21
- **Dependents:** 1
- **Priority Score:** 1000507.9
- **Functions:** 5/5 matched (target 14)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 13)
- **Missing types:** _none_

### 31. scan_expr

- **Target:** `syn.ScanExpr [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1000310.0
- **Functions:** 1/1 matched (target 69)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_
- **Lint issues:** 1

### 32. file

- **Target:** `syn.File`
- **Similarity:** 0.12
- **Dependents:** 1
- **Priority Score:** 1000208.8
- **Functions:** 2/2 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 33. ext

- **Target:** `syn.Ext [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 121410.0
- **Functions:** 2/7 matched (target 35)
- **Missing functions:** `parse_any`, `unraw`, `append`, `new_spanned`, `clone`
- **Types:** 0/7 matched (target 31)
- **Missing types:** `IdentExt`, `Token`, `TokenStreamExt`, `PunctExt`, `Sealed`, `PeekFn`, `IdentAny`

### 34. parse

- **Target:** `syn.Parse`
- **Similarity:** 0.18
- **Dependents:** 0
- **Priority Score:** 96008.2
- **Functions:** 42/51 matched (target 61)
- **Missing functions:** `clone`, `peek2`, `peek3`, `parse`, `__parse_scoped`, `parse_scoped`, `parse`, `clone`, `fmt`
- **Types:** 9/9 matched (target 19)
- **Missing types:** _none_
- **Lint issues:** 2

### 35. parse_quote

- **Target:** `syn.ParseQuote`
- **Similarity:** 0.01
- **Dependents:** 0
- **Priority Score:** 91109.9
- **Functions:** 1/10 matched
- **Missing functions:** `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`, `parse`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 36. export

- **Target:** `syn.Export`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 60800.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 2/8 matched (target 3)
- **Missing types:** `bool`, `str`, `Span`, `TokenStream2`, `TokenStream`, `private`

### 37. data

- **Target:** `syn.Data`
- **Similarity:** 0.16
- **Dependents:** 0
- **Priority Score:** 22108.4
- **Functions:** 16/18 matched (target 23)
- **Missing functions:** `into_iter`, `into_iter`
- **Types:** 3/3 matched (target 14)
- **Missing types:** _none_
- **Lint issues:** 1

### 38. whitespace

- **Target:** `syn.Whitespace [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 20210.0
- **Functions:** 0/2 matched (target 31)
- **Missing functions:** `skip`, `is_whitespace`
- **Types:** 0/0 matched (target 9)
- **Missing types:** _none_

### 39. fixup

- **Target:** `syn.Fixup [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 11810.0
- **Functions:** 15/16 matched (target 24)
- **Missing functions:** `clone`
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Lint issues:** 1

### 40. stmt

- **Target:** `syn.Stmt [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 11210.0
- **Functions:** 10/11 matched (target 20)
- **Missing functions:** `parse`
- **Types:** 1/1 matched (target 9)
- **Missing types:** _none_

### 41. meta

- **Target:** `syn.Meta`
- **Similarity:** 0.21
- **Dependents:** 0
- **Priority Score:** 10707.9
- **Functions:** 5/6 matched (target 7)
- **Missing functions:** `parse_nested_meta`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 42. print

- **Target:** `print.Print`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 10209.4
- **Functions:** 1/1 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/1 matched (target 8)
- **Missing types:** `TokensOrDefault`

### 43. buffer

- **Target:** `syn.Buffer`
- **Similarity:** 0.22
- **Dependents:** 0
- **Priority Score:** 3507.8
- **Functions:** 31/31 matched (target 33)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 10)
- **Missing types:** _none_

### 44. mac

- **Target:** `syn.Mac`
- **Similarity:** 0.19
- **Dependents:** 0
- **Priority Score:** 808.1
- **Functions:** 8/8 matched (target 10)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 45. tt

- **Target:** `syn.Tt`
- **Similarity:** 0.09
- **Dependents:** 0
- **Priority Score:** 609.1
- **Functions:** 4/4 matched (target 17)
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_

### 46. thread

- **Target:** `syn.Thread`
- **Similarity:** 0.08
- **Dependents:** 0
- **Priority Score:** 509.2
- **Functions:** 4/4 matched (target 7)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 47. restriction

- **Target:** `syn.Restriction`
- **Similarity:** 0.11
- **Dependents:** 0
- **Priority Score:** 508.9
- **Functions:** 5/5 matched (target 14)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 12)
- **Missing types:** _none_
- **Lint issues:** 2

### 48. discouraged

- **Target:** `syn.Discouraged`
- **Similarity:** 0.01
- **Dependents:** 0
- **Priority Score:** 409.9
- **Functions:** 2/2 matched (target 6)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 4)
- **Missing types:** _none_

### 49. op

- **Target:** `syn.Op`
- **Similarity:** 0.22
- **Dependents:** 0
- **Priority Score:** 407.8
- **Functions:** 4/4 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 35)
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

