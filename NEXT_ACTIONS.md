# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 53/55 (96.4%)
- **Function parity:** 755/1083 matched (target 2823) — 69.7%
- **Class/type parity:** 95/121 matched (target 812) — 78.5%
- **Combined symbol parity:** 850/1204 matched (target 3635) — 70.6%
- **Average inline-code cosine:** 0.51 (function body across 53 matched files)
- **Average documentation cosine:** 0.32 (doc text across 53 matched files)
- **Cheat-zeroed Files:** 4
- **Critical Issues:** 29 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. token
- **Similarity:** 0.60 (needs 25% improvement)
- **Dependencies:** 17
- **Priority Score:** 17042204.0
- **Functions:** 13/17 matched (target 685)
- **Missing functions:** `Group`, `fmt`, `eq`, `hash`
- **Types:** 5/5 matched (target 305)
- **Missing types:** _none_
- **Symbol Deficit:** 4 (functions: 4, types: 0)
- **Action:** Deep review - likely missing major functionality

### 2. punctuated
- **Similarity:** 0.44 (needs 41% improvement)
- **Dependencies:** 13
- **Priority Score:** 13076806.0
- **Functions:** 49/54 matched (target 296)
- **Missing functions:** `eq`, `hash`, `fmt`, `index`, `index_mut`
- **Types:** 12/14 matched (target 34)
- **Missing types:** `Item`, `Output`
- **Symbol Deficit:** 7 (functions: 5, types: 2)
- **Action:** Deep review - likely missing major functionality

### 3. expr
- **Similarity:** 0.42 (needs 43% improvement)
- **Dependencies:** 10
- **Priority Score:** 10126606.0
- **Functions:** 54/65 matched (target 199)
- **Missing functions:** `parse_without_eager_brace`, `parse_with_earlier_boundary_rule`, `peek`, `from`, `eq`, `hash`, `fmt`, `clone`, `expr_group`, `parse_obsolete`, `parse_multiple`
- **Types:** 0/1 matched (target 56)
- **Missing types:** `AllowStruct`
- **Symbol Deficit:** 12 (functions: 11, types: 1)
- **Action:** Deep review - likely missing major functionality

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. token

- **Target:** `token.Token`
- **Similarity:** 0.60
- **Dependents:** 17
- **Priority Score:** 17042204.0
- **Functions:** 13/17 matched (target 685)
- **Missing functions:** `Group`, `fmt`, `eq`, `hash`
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
- **Similarity:** 0.44
- **Dependents:** 13
- **Priority Score:** 13076806.0
- **Functions:** 49/54 matched (target 296)
- **Missing functions:** `eq`, `hash`, `fmt`, `index`, `index_mut`
- **Types:** 12/14 matched (target 34)
- **Missing types:** `Item`, `Output`

### 4. expr

- **Target:** `syn.Expr`
- **Similarity:** 0.42
- **Dependents:** 10
- **Priority Score:** 10126606.0
- **Functions:** 54/65 matched (target 199)
- **Missing functions:** `parse_without_eager_brace`, `parse_with_earlier_boundary_rule`, `peek`, `from`, `eq`, `hash`, `fmt`, `clone`, `expr_group`, `parse_obsolete`, `parse_multiple`
- **Types:** 0/1 matched (target 56)
- **Missing types:** `AllowStruct`

### 5. path

- **Target:** `syn.Path`
- **Similarity:** 0.56
- **Dependents:** 9
- **Priority Score:** 9092804.0
- **Functions:** 18/26 matched (target 57)
- **Missing functions:** `clone`, `print_path`, `print_path_segment`, `print_path_arguments`, `print_angle_bracketed_generic_arguments`, `print_parenthesized_generic_arguments`, `print_qpath`, `conditionally_print_turbofish`
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
- **Similarity:** 0.71
- **Dependents:** 5
- **Priority Score:** 5021403.0
- **Functions:** 7/8 matched (target 23)
- **Missing functions:** `fmt`
- **Types:** 5/6 matched (target 12)
- **Missing types:** `Token`

### 9. lit

- **Target:** `syn.Lit`
- **Similarity:** 0.56
- **Dependents:** 5
- **Priority Score:** 5014204.5
- **Functions:** 37/38 matched (target 138)
- **Missing functions:** `fmt`
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

### 13. gen.visit_mut

- **Target:** `gen.VisitMut`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 2909010.0
- **Functions:** 0/189 matched (target 113)
- **Missing functions:** `visit_abi_mut`, `visit_angle_bracketed_generic_arguments_mut`, `visit_arm_mut`, `visit_assoc_const_mut`, `visit_assoc_type_mut`, `visit_attr_style_mut`, `visit_attribute_mut`, `visit_attributes_mut`, `visit_bare_fn_arg_mut`, `visit_bare_variadic_mut`, `visit_bin_op_mut`, `visit_block_mut`, `visit_bound_lifetimes_mut`, `visit_captured_param_mut`, `visit_const_param_mut`, `visit_constraint_mut`, `visit_data_mut`, `visit_data_enum_mut`, `visit_data_struct_mut`, `visit_data_union_mut`, `visit_derive_input_mut`, `visit_expr_mut`, `visit_expr_array_mut`, `visit_expr_assign_mut`, `visit_expr_async_mut`, `visit_expr_await_mut`, `visit_expr_binary_mut`, `visit_expr_block_mut`, `visit_expr_break_mut`, `visit_expr_call_mut`, `visit_expr_cast_mut`, `visit_expr_closure_mut`, `visit_expr_const_mut`, `visit_expr_continue_mut`, `visit_expr_field_mut`, `visit_expr_for_loop_mut`, `visit_expr_group_mut`, `visit_expr_if_mut`, `visit_expr_index_mut`, `visit_expr_infer_mut`, `visit_expr_let_mut`, `visit_expr_lit_mut`, `visit_expr_loop_mut`, `visit_expr_macro_mut`, `visit_expr_match_mut`, `visit_expr_method_call_mut`, `visit_expr_paren_mut`, `visit_expr_path_mut`, `visit_expr_range_mut`, `visit_expr_raw_addr_mut`, `visit_expr_reference_mut`, `visit_expr_repeat_mut`, `visit_expr_return_mut`, `visit_expr_struct_mut`, `visit_expr_try_mut`, `visit_expr_try_block_mut`, `visit_expr_tuple_mut`, `visit_expr_unary_mut`, `visit_expr_unsafe_mut`, `visit_expr_while_mut`, `visit_expr_yield_mut`, `visit_field_mut`, `visit_field_mutability_mut`, `visit_field_pat_mut`, `visit_field_value_mut`, `visit_fields_mut`, `visit_fields_named_mut`, `visit_fields_unnamed_mut`, `visit_file_mut`, `visit_fn_arg_mut`, `visit_foreign_item_mut`, `visit_foreign_item_fn_mut`, `visit_foreign_item_macro_mut`, `visit_foreign_item_static_mut`, `visit_foreign_item_type_mut`, `visit_generic_argument_mut`, `visit_generic_param_mut`, `visit_generics_mut`, `visit_ident_mut`, `visit_impl_item_mut`, `visit_impl_item_const_mut`, `visit_impl_item_fn_mut`, `visit_impl_item_macro_mut`, `visit_impl_item_type_mut`, `visit_impl_restriction_mut`, `visit_index_mut`, `visit_item_mut`, `visit_item_const_mut`, `visit_item_enum_mut`, `visit_item_extern_crate_mut`, `visit_item_fn_mut`, `visit_item_foreign_mod_mut`, `visit_item_impl_mut`, `visit_item_macro_mut`, `visit_item_mod_mut`, `visit_item_static_mut`, `visit_item_struct_mut`, `visit_item_trait_mut`, `visit_item_trait_alias_mut`, `visit_item_type_mut`, `visit_item_union_mut`, `visit_item_use_mut`, `visit_label_mut`, `visit_lifetime_mut`, `visit_lifetime_param_mut`, `visit_lit_mut`, `visit_lit_bool_mut`, `visit_lit_byte_mut`, `visit_lit_byte_str_mut`, `visit_lit_cstr_mut`, `visit_lit_char_mut`, `visit_lit_float_mut`, `visit_lit_int_mut`, `visit_lit_str_mut`, `visit_local_mut`, `visit_local_init_mut`, `visit_macro_mut`, `visit_macro_delimiter_mut`, `visit_member_mut`, `visit_meta_mut`, `visit_meta_list_mut`, `visit_meta_name_value_mut`, `visit_parenthesized_generic_arguments_mut`, `visit_pat_mut`, `visit_pat_ident_mut`, `visit_pat_or_mut`, `visit_pat_paren_mut`, `visit_pat_reference_mut`, `visit_pat_rest_mut`, `visit_pat_slice_mut`, `visit_pat_struct_mut`, `visit_pat_tuple_mut`, `visit_pat_tuple_struct_mut`, `visit_pat_type_mut`, `visit_pat_wild_mut`, `visit_path_mut`, `visit_path_arguments_mut`, `visit_path_segment_mut`, `visit_pointer_mutability_mut`, `visit_precise_capture_mut`, `visit_predicate_lifetime_mut`, `visit_predicate_type_mut`, `visit_qself_mut`, `visit_range_limits_mut`, `visit_receiver_mut`, `visit_return_type_mut`, `visit_signature_mut`, `visit_span_mut`, `visit_static_mutability_mut`, `visit_stmt_mut`, `visit_stmt_macro_mut`, `visit_token_stream_mut`, `visit_trait_bound_mut`, `visit_trait_bound_modifier_mut`, `visit_trait_item_mut`, `visit_trait_item_const_mut`, `visit_trait_item_fn_mut`, `visit_trait_item_macro_mut`, `visit_trait_item_type_mut`, `visit_type_mut`, `visit_type_array_mut`, `visit_type_bare_fn_mut`, `visit_type_group_mut`, `visit_type_impl_trait_mut`, `visit_type_infer_mut`, `visit_type_macro_mut`, `visit_type_never_mut`, `visit_type_param_mut`, `visit_type_param_bound_mut`, `visit_type_paren_mut`, `visit_type_path_mut`, `visit_type_ptr_mut`, `visit_type_reference_mut`, `visit_type_slice_mut`, `visit_type_trait_object_mut`, `visit_type_tuple_mut`, `visit_un_op_mut`, `visit_use_glob_mut`, `visit_use_group_mut`, `visit_use_name_mut`, `visit_use_path_mut`, `visit_use_rename_mut`, `visit_use_tree_mut`, `visit_variadic_mut`, `visit_variant_mut`, `visit_vis_restricted_mut`, `visit_visibility_mut`, `visit_where_clause_mut`, `visit_where_predicate_mut`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 14. item

- **Target:** `syn.Item`
- **Similarity:** 0.56
- **Dependents:** 2
- **Priority Score:** 2013204.5
- **Functions:** 27/28 matched (target 119)
- **Missing functions:** `parse_foreign_item_type`
- **Types:** 4/4 matched (target 61)
- **Missing types:** _none_
- **Lint issues:** 1

### 15. generics

- **Target:** `syn.Generics`
- **Similarity:** 0.67
- **Dependents:** 2
- **Priority Score:** 2012503.2
- **Functions:** 15/15 matched (target 93)
- **Missing functions:** _none_
- **Types:** 9/10 matched (target 33)
- **Missing types:** `Item`
- **Lint issues:** 1

### 16. precedence

- **Target:** `syn.Precedence`
- **Similarity:** 0.66
- **Dependents:** 2
- **Priority Score:** 2010703.4
- **Functions:** 5/6 matched
- **Missing functions:** `clone`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 17. pat

- **Target:** `syn.Pat`
- **Similarity:** 0.79
- **Dependents:** 2
- **Priority Score:** 2002402.1
- **Functions:** 23/23 matched (target 65)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 22)
- **Missing types:** _none_

### 18. gen.visit

- **Target:** `gen.Visit`
- **Similarity:** 0.19
- **Dependents:** 1
- **Priority Score:** 1518908.1
- **Functions:** 138/188 matched (target 147)
- **Missing functions:** `visit_arm`, `visit_bin_op`, `visit_expr_array`, `visit_expr_assign`, `visit_expr_async`, `visit_expr_await`, `visit_expr_binary`, `visit_expr_block`, `visit_expr_break`, `visit_expr_call`, `visit_expr_cast`, `visit_expr_closure`, `visit_expr_const`, `visit_expr_continue`, `visit_expr_field`, `visit_expr_for_loop`, `visit_expr_group`, `visit_expr_if`, `visit_expr_index`, `visit_expr_infer`, `visit_expr_let`, `visit_expr_lit`, `visit_expr_loop`, `visit_expr_macro`, `visit_expr_match`, `visit_expr_method_call`, `visit_expr_paren`, `visit_expr_path`, `visit_expr_range`, `visit_expr_raw_addr`, `visit_expr_reference`, `visit_expr_repeat`, `visit_expr_return`, `visit_expr_struct`, `visit_expr_try`, `visit_expr_try_block`, `visit_expr_tuple`, `visit_expr_unary`, `visit_expr_unsafe`, `visit_expr_while`, `visit_expr_yield`, `visit_field_value`, `visit_foreign_item`, `visit_foreign_item_fn`, `visit_foreign_item_macro`, `visit_foreign_item_static`, `visit_foreign_item_type`, `visit_impl_restriction`, `visit_item_extern_crate`, `visit_item_foreign_mod`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Lint issues:** 15

### 19. gen.fold

- **Target:** `gen.Fold`
- **Similarity:** 0.21
- **Dependents:** 1
- **Priority Score:** 1409107.9
- **Functions:** 151/190 matched (target 156)
- **Missing functions:** `fold_expr_array`, `fold_expr_assign`, `fold_expr_async`, `fold_expr_await`, `fold_expr_break`, `fold_expr_call`, `fold_expr_cast`, `fold_expr_closure`, `fold_expr_const`, `fold_expr_continue`, `fold_expr_field`, `fold_expr_group`, `fold_expr_if`, `fold_expr_index`, `fold_expr_infer`, `fold_expr_let`, `fold_expr_lit`, `fold_expr_macro`, `fold_expr_match`, `fold_expr_method_call`, `fold_expr_paren`, `fold_expr_reference`, `fold_expr_repeat`, `fold_expr_return`, `fold_expr_struct`, `fold_expr_try`, `fold_expr_try_block`, `fold_expr_tuple`, `fold_expr_unsafe`, `fold_expr_yield`, `fold_foreign_item`, `fold_foreign_item_fn`, `fold_foreign_item_macro`, `fold_foreign_item_static`, `fold_foreign_item_type`, `fold_impl_restriction`, `fold_item_extern_crate`, `fold_item_foreign_mod`, `fold_vec`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 20. attr

- **Target:** `syn.Attr`
- **Similarity:** 0.67
- **Dependents:** 1
- **Priority Score:** 1042703.3
- **Functions:** 20/23 matched (target 47)
- **Missing functions:** `is_outer`, `is_inner`, `fmt`
- **Types:** 3/4 matched (target 16)
- **Missing types:** `Ret`
- **Lint issues:** 1

### 21. ty

- **Target:** `syn.Type`
- **Similarity:** 0.44
- **Dependents:** 1
- **Priority Score:** 1040705.6
- **Functions:** 3/7 matched (target 49)
- **Missing functions:** `ambig_ty`, `parse_bounds`, `parse_bare_fn_arg`, `parse_bare_variadic`
- **Types:** 0/0 matched (target 24)
- **Missing types:** _none_
- **Lint issues:** 1

### 22. scan_expr

- **Target:** `syn.ScanExpr`
- **Similarity:** 0.58
- **Dependents:** 1
- **Priority Score:** 1020304.2
- **Functions:** 1/1 matched (target 89)
- **Missing functions:** _none_
- **Types:** 0/2 matched (target 3)
- **Missing types:** `Input`, `Action`
- **Lint issues:** 1

### 23. gen.debug

- **Target:** `gen.Debug`
- **Similarity:** 0.02
- **Dependents:** 1
- **Priority Score:** 1010209.8
- **Functions:** 1/2 matched (target 27)
- **Missing functions:** `fmt`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 24. group

- **Target:** `syn.Group`
- **Similarity:** 0.89
- **Dependents:** 1
- **Priority Score:** 1000901.1
- **Functions:** 5/5 matched (target 8)
- **Missing functions:** _none_
- **Types:** 4/4 matched
- **Missing types:** _none_

### 25. bigint

- **Target:** `syn.BigInt`
- **Similarity:** 0.67
- **Dependents:** 1
- **Priority Score:** 1000603.3
- **Functions:** 5/5 matched (target 8)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 26. derive

- **Target:** `syn.Derive`
- **Similarity:** 0.74
- **Dependents:** 1
- **Priority Score:** 1000502.6
- **Functions:** 5/5 matched (target 14)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 13)
- **Missing types:** _none_

### 27. spanned

- **Target:** `syn.Spanned`
- **Similarity:** 0.97
- **Dependents:** 1
- **Priority Score:** 1000300.3
- **Functions:** 1/1 matched (target 3)
- **Missing functions:** _none_
- **Types:** 2/2 matched
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

### 32. parse

- **Target:** `syn.Parse`
- **Similarity:** 0.54
- **Dependents:** 0
- **Priority Score:** 54204.6
- **Functions:** 30/33 matched (target 63)
- **Missing functions:** `fmt`, `eq`, `hash`
- **Types:** 7/9 matched (target 17)
- **Missing types:** `Target`, `Output`
- **Lint issues:** 2

### 33. data

- **Target:** `syn.Data`
- **Similarity:** 0.57
- **Dependents:** 0
- **Priority Score:** 31404.3
- **Functions:** 10/11 matched (target 22)
- **Missing functions:** `into_iter`
- **Types:** 1/3 matched (target 12)
- **Missing types:** `Item`, `IntoIter`
- **Lint issues:** 1

### 34. drops

- **Target:** `syn.Drops [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 30910.0
- **Functions:** 4/5 matched (target 9)
- **Missing functions:** `test_needs_drop`
- **Types:** 2/4 matched (target 2)
- **Missing types:** `Target`, `NeedsDrop`
- **Tests:** 0/1 matched

### 35. buffer

- **Target:** `syn.Buffer`
- **Similarity:** 0.69
- **Dependents:** 0
- **Priority Score:** 23503.1
- **Functions:** 29/31 matched
- **Missing functions:** `new`, `eq`
- **Types:** 4/4 matched (target 10)
- **Missing types:** _none_

### 36. discouraged

- **Target:** `syn.Discouraged`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 20410.0
- **Functions:** 0/2 matched (target 4)
- **Missing functions:** `advance_to`, `parse_any_delimiter`
- **Types:** 2/2 matched (target 4)
- **Missing types:** _none_

### 37. ext

- **Target:** `syn.Ext`
- **Similarity:** 0.82
- **Dependents:** 0
- **Priority Score:** 11401.8
- **Functions:** 7/7 matched (target 12)
- **Missing functions:** _none_
- **Types:** 6/7 matched
- **Missing types:** `Token`

### 38. thread

- **Target:** `syn.Thread`
- **Similarity:** 0.39
- **Dependents:** 0
- **Priority Score:** 10506.1
- **Functions:** 3/4 matched (target 12)
- **Missing functions:** `fmt`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 39. fixup

- **Target:** `syn.Fixup`
- **Similarity:** 0.65
- **Dependents:** 0
- **Priority Score:** 1703.5
- **Functions:** 15/15 matched (target 24)
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Lint issues:** 1

### 40. stmt

- **Target:** `syn.Stmt`
- **Similarity:** 0.64
- **Dependents:** 0
- **Priority Score:** 803.6
- **Functions:** 7/7 matched (target 20)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 9)
- **Missing types:** _none_

### 41. mac

- **Target:** `syn.Mac`
- **Similarity:** 0.74
- **Dependents:** 0
- **Priority Score:** 802.6
- **Functions:** 8/8 matched (target 9)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 42. meta

- **Target:** `syn.Meta`
- **Similarity:** 0.65
- **Dependents:** 0
- **Priority Score:** 603.5
- **Functions:** 5/5 matched (target 7)
- **Missing functions:** _none_
- **Types:** 1/1 matched
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

