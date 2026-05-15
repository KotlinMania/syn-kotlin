# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 21/55 (38.2%)
- **Function parity:** 55/2571 matched (target 494) — 2.1%
- **Class/type parity:** 15/121 matched (target 272) — 12.4%
- **Combined symbol parity:** 70/2692 matched (target 766) — 2.6%
- **Average inline-code cosine:** 0.25 (function body across 21 matched files)
- **Average documentation cosine:** 0.33 (doc text across 21 matched files)
- **Cheat-zeroed Files:** 2
- **Critical Issues:** 18 files with <0.60 function similarity

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
- **Similarity:** 0.11 (needs 74% improvement)
- **Dependencies:** 13
- **Priority Score:** 13506809.0
- **Functions:** 17/54 matched (target 22)
- **Missing functions:** `first_mut`, `last_mut`, `get_mut`, `iter`, `iter_mut`, `pairs_mut`, `into_pairs`, `parse_terminated`, `parse_terminated_with`, `parse_separated_nonempty`, `parse_separated_nonempty_with`, `clone`, `clone_from`, `eq`, `hash`, `fmt`, `from_iter`, `extend`, `do_extend`, `into_iter`, `default`, `next`, `size_hint`, `next_back`, `clone_box`, `empty_punctuated_iter_mut`, `into_value`, `value`, `value_mut`, `punct`, `punct_mut`, `into_tuple`, `cloned`, `index`, `index_mut`, `fold`, `to_tokens`
- **Types:** 1/14 matched (target 3)
- **Missing types:** `Item`, `IntoIter`, `Pairs`, `PairsMut`, `IntoPairs`, `Iter`, `IterTrait`, `PrivateIter`, `IterMut`, `IterMutTrait`, `PrivateIterMut`, `Pair`, `Output`
- **Symbol Deficit:** 50 (functions: 37, types: 13)
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
- **Similarity:** 0.11
- **Dependents:** 13
- **Priority Score:** 13506809.0
- **Functions:** 17/54 matched (target 22)
- **Missing functions:** `first_mut`, `last_mut`, `get_mut`, `iter`, `iter_mut`, `pairs_mut`, `into_pairs`, `parse_terminated`, `parse_terminated_with`, `parse_separated_nonempty`, `parse_separated_nonempty_with`, `clone`, `clone_from`, `eq`, `hash`, `fmt`, `from_iter`, `extend`, `do_extend`, `into_iter`, `default`, `next`, `size_hint`, `next_back`, `clone_box`, `empty_punctuated_iter_mut`, `into_value`, `value`, `value_mut`, `punct`, `punct_mut`, `into_tuple`, `cloned`, `index`, `index_mut`, `fold`, `to_tokens`
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

### 7. lit

- **Target:** `syn.Lit`
- **Similarity:** 0.02
- **Dependents:** 5
- **Priority Score:** 5384210.0
- **Functions:** 4/38 matched (target 5)
- **Missing functions:** `parse`, `parse_with`, `respan_token_stream`, `respan_token_tree`, `set_span`, `suffix`, `token`, `base10_digits`, `base10_parse`, `from`, `fmt`, `debug`, `clone`, `parse_negative_lit`, `peek_impl`, `from_str_for_fuzzing`, `from_str`, `byte`, `next_chr`, `parse_lit_str`, `parse_lit_str_cooked`, `parse_lit_str_raw`, `parse_lit_byte_str`, `parse_lit_byte_str_cooked`, `parse_lit_byte_str_raw`, `parse_lit_c_str`, `parse_lit_c_str_cooked`, `parse_lit_c_str_raw`, `parse_lit_byte`, `parse_lit_char`, `backslash_x`, `backslash_u`, `parse_lit_int`, `parse_lit_float`
- **Types:** 0/4 matched (target 16)
- **Missing types:** `LitRepr`, `LitIntRepr`, `LitFloatRepr`, `StrStyle`

### 8. span

- **Target:** `syn.Span`
- **Similarity:** 0.00
- **Dependents:** 4
- **Priority Score:** 4010210.0
- **Functions:** 0/1 matched (target 9)
- **Missing functions:** `into_spans`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 9. error

- **Target:** `syn.Error`
- **Similarity:** 0.35
- **Dependents:** 3
- **Priority Score:** 3102206.5
- **Functions:** 8/14 matched (target 13)
- **Missing functions:** `new_at`, `fmt`, `clone`, `into_iter`, `next`, `extend`
- **Types:** 4/8 matched (target 4)
- **Missing types:** `_Test`, `Item`, `IntoIter`, `Iter`

### 10. generics

- **Target:** `syn.Generics [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2252510.0
- **Functions:** 0/15 matched (target 0)
- **Missing functions:** `default`, `make_where_clause`, `split_for_impl`, `next`, `as_turbofish`, `new`, `from`, `parse`, `parse_single`, `parse_multiple`, `do_parse`, `choose_generics_over_qpath`, `choose_generics_over_qpath_after_keyword`, `to_tokens`, `print_const_argument`
- **Types:** 0/10 matched (target 12)
- **Missing types:** `Lifetimes`, `Item`, `LifetimesMut`, `TypeParams`, `TypeParamsMut`, `ConstParams`, `ConstParamsMut`, `ImplGenerics`, `TypeGenerics`, `Turbofish`

### 11. precedence

- **Target:** `syn.Precedence`
- **Similarity:** 0.13
- **Dependents:** 2
- **Priority Score:** 2050708.6
- **Functions:** 1/6 matched (target 1)
- **Missing functions:** `of`, `prefix_attrs`, `clone`, `eq`, `partial_cmp`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 12. attr

- **Target:** `syn.Attr`
- **Similarity:** 0.08
- **Dependents:** 1
- **Priority Score:** 1252709.1
- **Functions:** 2/23 matched (target 11)
- **Missing functions:** `parse_args`, `parse_args_with`, `parse_outer`, `parse_inner`, `require_path_only`, `require_list`, `require_name_value`, `outer`, `is_outer`, `inner`, `is_inner`, `from`, `single_parse_inner`, `single_parse_outer`, `parse`, `parse_outermost_meta_path`, `parse_meta_after_path`, `parse_meta_list_after_path`, `parse_meta_name_value_after_path`, `fmt`, `to_tokens`
- **Types:** 0/4 matched (target 11)
- **Missing types:** `FilterAttrs`, `Ret`, `DisplayAttrStyle`, `DisplayPath`

### 13. ty

- **Target:** `syn.Type`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1070710.0
- **Functions:** 0/7 matched (target 4)
- **Missing functions:** `parse`, `without_plus`, `ambig_ty`, `parse_bounds`, `parse_bare_fn_arg`, `parse_bare_variadic`, `to_tokens`
- **Types:** 0/0 matched (target 23)
- **Missing types:** _none_

### 14. bigint

- **Target:** `syn.BigInt`
- **Similarity:** 0.32
- **Dependents:** 1
- **Priority Score:** 1030606.8
- **Functions:** 2/5 matched
- **Missing functions:** `new`, `add_assign`, `mul_assign`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 15. spanned

- **Target:** `syn.Spanned`
- **Similarity:** 0.97
- **Dependents:** 1
- **Priority Score:** 1010300.3
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Sealed`

### 16. sealed

- **Target:** `syn.Sealed`
- **Similarity:** 1.00
- **Dependents:** 1
- **Priority Score:** 1000100.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 17. data

- **Target:** `syn.Data`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 121409.4
- **Functions:** 2/11 matched (target 5)
- **Missing functions:** `iter`, `iter_mut`, `into_iter`, `next`, `clone`, `parse`, `parse_named`, `parse_unnamed`, `to_tokens`
- **Types:** 0/3 matched (target 16)
- **Missing types:** `Item`, `IntoIter`, `Members`

### 18. restriction

- **Target:** `syn.Restriction [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 40410.0
- **Functions:** 0/4 matched (target 0)
- **Missing functions:** `parse`, `parse_pub`, `is_some`, `to_tokens`
- **Types:** 0/0 matched (target 8)
- **Missing types:** _none_

### 19. thread

- **Target:** `syn.Thread`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 20507.0
- **Functions:** 2/4 matched (target 3)
- **Missing functions:** `fmt`, `clone`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 20. op

- **Target:** `syn.Op`
- **Similarity:** 0.37
- **Dependents:** 0
- **Priority Score:** 10206.3
- **Functions:** 1/2 matched
- **Missing functions:** `parse`
- **Types:** 0/0 matched (target 33)
- **Missing types:** _none_

### 21. print

- **Target:** `syn.Print`
- **Similarity:** 0.86
- **Dependents:** 0
- **Priority Score:** 201.4
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Next Commands

```bash
# Initialize task queue for systematic porting
cd tools/ast_distance
./ast_distance --init-tasks ../../tmp/syn/src rust ../../src/commonMain/kotlin/io/github/kotlinmania/syn kotlin tasks.json ../../AGENTS.md

# Get next high-priority task
./ast_distance --assign tasks.json <agent-id>
```
## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

