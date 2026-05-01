# BalanceIndicator

Minecraft 1.21.x PlaceholderAPI plugin that displays how much a player's Vault economy balance increased or decreased.

## Placeholder

```text
%balance_indicator%
```

The placeholder compares the player's current Vault balance with the last balance seen by the placeholder.

## Format Examples

Default:

```yaml
format:
  positive: "{sign}{amount}"
  negative: "{sign}{amount}"
```

Outputs:

```text
+250
-128
```

Suffix style:

```yaml
format:
  positive: "{amount}{sign}"
  negative: "{amount}{sign}"
```

Outputs:

```text
250+
128-
```

## Build

```bash
mvn package
```

The jar will be created in `target/`.

## Requirements

- Java 21
- Paper or compatible 1.21.x server
- Vault
- A Vault-compatible economy plugin
- PlaceholderAPI
