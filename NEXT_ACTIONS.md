# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Current Progress:** 9.1% (5/55 files)
- **Matched Files:** 5
- **Average Similarity:** 0.15
- **Critical Issues:** 5 files with <0.60 similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. token
- **Similarity:** 0.03 (needs 82% improvement)
- **Dependencies:** 17
- **Priority Score:** 16.5
- **Action:** Deep review - likely missing major functionality

### 2. ident
- **Similarity:** 0.05 (needs 80% improvement)
- **Dependencies:** 14
- **Priority Score:** 13.3
- **Action:** Deep review - likely missing major functionality

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

1. **punctuated** (13 deps)
   - Path: `punctuated.rs`
   - Essential for 13 other files

2. **expr** (10 deps)
   - Path: `expr.rs`
   - Essential for 10 other files

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
