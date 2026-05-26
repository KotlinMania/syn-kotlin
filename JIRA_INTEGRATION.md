# Jira ordered_priority and Scaffold Scripts Reference

This document contains detailed descriptions of the Jira-prioritized roadmap, `port_priority.json` schema, and scaffold management scripts extracted from `AGENTS.md`.

## Pick what to port next — Jira `ordered_priority` artifacts

Two artifacts at the workspace root answer "what should I port next?" without anyone needing to hit Jira directly:

- **[`PORT_PRIORITY.md`](./PORT_PRIORITY.md)** — human-readable prioritized roadmap.
- **[`port_priority.json`](./port_priority.json)** — machine-readable dump of the same data. Schema below.

Both are auto-generated. **Do not hand-edit them.** If they look stale, regenerate. If they look wrong, fix the generator (`scaffold/analysis/generate_port_priority.py`) or the upstream Jira data — never patch the artifacts in place.

**The headline field: `ordered_priority`** — every Initiative carries a single integer:
- **`0`** — do not port. No callers, not needed (test-only crates, build-script-only crates, unreferenced utilities). Don't port unless something downstream changes.
- **`1`** — port first. Highest-leverage item.
- **`2..N`** — ordered descending by impact. Lower integer = port sooner.

`tier == preexisting` items (already-shipped `*-kotlin` repos) never get `0`; they're maintenance-mode regardless of who depends on them.

The integer also lives in Jira as the **`Ordered Priority`** custom field (`customfield_10153` on this site), so JQL works:

```
"Ordered Priority" = 1                                  # the single most important port
"Ordered Priority" <= 10                                # Wave 1
"Ordered Priority" > 0 ORDER BY "Ordered Priority" ASC  # full work order
"Ordered Priority" = 0                                  # the skip-list
```

### Regenerate Priority Files

Both tasks are idempotent; re-running over an unchanged graph produces byte-identical outputs:

```bash
python scaffold/analysis/generate_port_priority.py  # graph → port_priority.{json,md}
python scaffold/jira/apply_ordered_priority.py      # JSON ranks → Jira field
```

Auth via `scaffold/_auth.py` (chmod 600). The generator hits the live Jira graph (project `COD`, issuetype `Initiative`); the apply script reads `ordered_priority` straight from `port_priority.json` and pushes it to Jira. Optional generator flags:

- `--use-cache` — reuse `scaffold/graph_cache.json` if it's <5 min old (skips the Jira fetch)
- `--cache-age N` — change cache freshness threshold (seconds)
- `--top N` — number of rows in the top-N table (default 30)
- `--out-dir PATH` — write outputs somewhere other than the kotlinmania root

The field-ID cache for the apply script lives at `scaffold/.ordered_priority_field_id`. Delete it if you ever migrate to a different Jira site.

### What the generator computes

For every Initiative:

| Field | Meaning |
|---|---|
| `ordered_priority` | The pick-one-integer field above. **0 = skip**, **1 = port first**, higher = lower priority. |
| `layer` | Topological depth from leaves (Kahn-style BFS). L0 = no prerequisites. |
| `direct_dependent_count` | Number of Initiatives this one directly blocks (out-degree on the Blocks graph). |
| `transitive_dependent_count` | Total Initiatives reachable downstream via Blocks edges. The headline impact metric. |
| `prerequisite_count` | In-degree on the Blocks graph — how many things must finish first. |
| `cluster` | Weakly-connected component ID (treats Blocks ∪ Relates as undirected). |

The "top 3" highlight = filter to `layer == 0`, `status != "Done"`, `ordered_priority > 0`; sort ascending by `ordered_priority`; take three.

### `port_priority.json` schema

Stable contracts: `schema_version` bumps if any field is renamed/removed; key references use the Jira issue key `COD-NNN` verbatim; `blocks_edges` is directed `[blocker, blocked]`; `relates_edges` is symmetric with `a < b`; every list-valued node field is sorted for diff stability; `tier` derives from labels — `tier-direct` = codex-rs depends on this external crate, `tier-internal` = a sub-module of codex-rs itself, `tier-preexisting` = `*-kotlin` already exists and needs maintenance, `unknown` = no tier label.

```jsonc
{
  "schema_version": 1,
  "nodes": {
    "COD-128": {
      "key": "COD-128",
      "summary": "Port the serde crate",
      "status": "To Do",
      "labels": ["codex-port", "crate-serde", "tier-direct"],
      "tier": "direct",                       // direct | internal | preexisting | unknown
      "layer": 0,
      "ordered_priority": 1,                  // 0 = skip; 1+ = rank, lower = port sooner
      "direct_dependents": ["COD-26", ...],   // sorted list of keys
      "direct_dependent_count": 101,
      "transitive_dependent_count": 187,
      "prerequisites": [],                    // sorted list of keys
      "prerequisite_count": 0,
      "cluster": "COD-128"                    // anchor key of the WCC this node lives in
    }
    // ... one entry per Initiative
  },
  "blocks_edges":  [["COD-128", "COD-26"], ...],   // [blocker, blocked]
  "relates_edges": [["COD-3",   "COD-99"], ...],   // sorted alphabetically
  "summary": {
    "node_count": 322,
    "blocks_edge_count": 1919,
    "relates_edge_count": 544,
    "max_layer": 8,
    "status_distribution": {"To Do": 322},
    "tier_distribution":   {"direct": 191, "internal": 107, "preexisting": 22, "unknown": 2},
    "cluster_count": 17,
    "singleton_cluster_count": 16,
    "largest_cluster_size": 306
  }
}
```

### When the priority artifacts are the wrong tool
The graph captures *what blocks what* at the crate level. It does **not** capture:
- Function-level call graphs inside a crate (per-Initiative Stories + Tasks live under each Initiative — query Jira directly).
- The state of any individual `*-kotlin/` repo's port. **Each repo's `AGENTS.md` / `CLAUDE.md` / `README.md` tells that story.**

Use `port_priority.json` for "where do I start" decisions. For everything else, defer to per-repo docs.

---

## Scaffold script index

Jira API helpers live under `scaffold/jira/`; `scaffold/jira/_jira_http.py` provides throttled + retrying API calls (default 6 req/s, exponential backoff on 429/5xx). `scaffold/build_surface.py` intentionally remains at the scaffold root.

| Script | Purpose |
|---|---|
| `analysis/generate_port_priority.py` | Graph fetch + analysis → `port_priority.{json,md}`. |
| `jira/apply_ordered_priority.py` | Pushes `ordered_priority` from JSON onto every Initiative as the `Ordered Priority` custom field. Self-wires to COD screens on first run. |
| `analysis/graph_analyze.py` | Lower-level analysis library + ad-hoc CLI (`--layer N`, `--cluster KEY`, etc.). |
| `analysis/audit_codex_internal.py` | Parse the codex-rs Cargo workspace into `codex_internal_deps.json` (107 internal crates). |
| `analysis/audit_initiative_deps.py` | Parse all 215 external crates' Cargo.tomls into `initiative_deps.json`. |
| `jira/create_internal_initiatives.py` | One-shot to seed the 107 internal Initiatives in Jira. |
| `jira/apply_initiative_deps.py` | Apply Blocks/Relates links between external Initiatives based on the audit. |
| `jira/apply_internal_links.py` | Two-wave linker for internal→internal and internal→external. Idempotent. |
| `jira/link_codex_kotlin_assembly.py` | Link the COD-2 codex-kotlin assembly Initiative to all 107 internal sub-modules. |
| `jira/activate_internal_crate.py` / `jira/activate_all_internal.py` | Per-crate Story+Task creator + bulk driver. |
| `jira/markdown_to_adf.py` | Markdown → ADF (Atlassian Document Format) converter. Used when posting Jira descriptions / comments. |

### Description hygiene
Atlassian Intelligence (Rovo) curates Initiative descriptions. **Never `PUT` / `PATCH` an existing description** — append via `POST /comment` instead. The scaffold scripts respect this; if you write new ones, do too.
