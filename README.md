# GriefPrevention3D Documentation

## Introduction

GriefPrevention3D is a comprehensive grief prevention plugin for Minecraft servers. It allows players to protect their builds by creating 3D claims, managing trust levels, and more. This plugin is inspired by the original GriefPrevention plugin but with the added dimension of 3D claims.

## Features

*   **3D Claims:** Create cuboid claims to protect your builds in all three dimensions.
*   **Claim Block System:** Earn claim blocks over time to expand your protected area.
*   **Trust System:** Grant different levels of permission to other players in your claims.
*   **Subclaims:** Divide your claims into smaller, manageable sub-areas.
*   **Advanced Protection:** Protects against explosions, pistons, and fluid flow.
*   **FAWE Integration:** Save and load claims as schematics using FastAsyncWorldEdit.
*   **Configurable:** Customize the plugin to your server's needs through the `config.yml` file.
*   **In-game visualization** – "Stick-Rightclick" outlines, claim borders, and info messages

## Installation

1.  Place the `GriefPrevention3D.jar` file in your server's `plugins` directory.
2.  Install the latest version of [FastAsyncWorldEdit](https://www.spigotmc.org/resources/fastasyncworldedit.13932/).
3.  Restart the server and use a golden shovel on two corners to start claiming!

## Commands

| Command                               | Description                                      |
| ------------------------------------- | ------------------------------------------------ |
| `/claim create`                         | Creates a new claim.                             |
| `/claim abandon`                        | Deletes the claim you are standing in.           |
| `/claim trust <player> <level>`         | Trusts a player in your claim.                   |
| `/claim untrust <player>`               | Untrusts a player in your claim.                 |
| `/claim subdivide`                      | Toggles subdivide mode.                          |
| `/claim blocks`                         | Shows your claim block balance.                  |
| `/claim giveblocks <player> <amount>`   | Gives claim blocks to a player. (Admin only)     |
| `/claim saveall`                        | Saves all claims to schematics. (Admin only)     |
| `/claim loadall`                        | Loads all claims from schematics. (Admin only)   |

## Permissions

| Permission                          | Description                                      | Default |
| ----------------------------------- | ------------------------------------------------ | ------- |
| `griefprevention3d.claim.create`    | Allows players to create claims.                 | `true`  |
| `griefprevention3d.claim.abandon`   | Allows players to abandon their claims.          | `true`  |
| `griefprevention3d.claim.trust`     | Allows players to trust other players.           | `true`  |
| `griefprevention3d.claim.untrust`   | Allows players to untrust other players.         | `true`  |
| `griefprevention3d.claim.subdivide` | Allows players to subdivide their claims.        | `true`  |
| `griefprevention3d.claim.blocks`    | Allows players to check their claim block balance. | `true`  |
| `griefprevention3d.visualize`       | Allows players to visualize claims with a stick. | `true`  |
| `griefprevention3d.admin.giveblocks` | Allows admins to give claim blocks.              | `op`    |
| `griefprevention3d.admin.saveall`   | Allows admins to save all claims.                | `op`    |
| `griefprevention3d.admin.loadall`  | Allows admins to load all claims.                | `op`    |
| `griefprevention3d.max-accrued-claim-blocks.<tier_name>` | Sets a player's maximum claim blocks based on a configured tier. | `false` |

## Configuration

The `config.yml` file allows you to customize various aspects of the plugin.

```yaml
claim-block-accrual:
  amount: 100
  max-limit: 1000

new-player-claim-blocks: 100

visualization:
  particle: "FLAME"

messages:
  no-permission-break: "&cYou do not have permission to break blocks in this claim."
  no-permission-place: "&cYou do not have permission to place blocks in this claim."
  no-permission-interact: "&cYou do not have permission to interact with blocks in this claim."

max-claim-blocks-by-permission:
  default: 1000
  griefprevention3d.max-accrued-claim-blocks.tier1: 2000
  griefprevention3d.max-accrued-claim-blocks.tier2: 5000
```

*   **`claim-block-accrual.amount`:** The number of claim blocks players receive every hour.
*   **`claim-block-accrual.max-limit`:** The default maximum number of claim blocks a player can have if no specific permission is found.
*   **`new-player-claim-blocks`:** The number of claim blocks a new player starts with.
*   **`visualization.particle`:** The particle effect used to visualize claim boundaries.
*   **`messages`:** Customizable messages for various plugin actions.
*   **`max-claim-blocks-by-permission`:** This section defines different maximum claim block limits based on permissions. The plugin will check if a player has any of the permissions listed here (e.g., `griefprevention3d.max-accrued-claim-blocks.tier1`) and apply the highest corresponding limit. If no specific permission is found, the `default` value will be used.
