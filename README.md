# Celestial-Clash
Celestial Clash is a game mode that combines digging, intense battles and accumulation of points implemented using the Spigot plugin
1. Game Start:
 - Players appear at the starting position near the triggers.
 - A countdown timer of 7 seconds starts.
 - During the countdown, players cannot move.
 - Each activation of the trigger randomly changes the location of items in the pit.
2. Excavation Phase:
 - After the 7-second countdown ends, a new countdown of 60 seconds begins.
 - Players head to the pit and start digging.
 - There are 40 items in each pit.
 - Within the allotted 60 seconds, each player can guarantee to extract a minimum of 10 items.
 - The items found during the excavation are used for battle in the arena.
 - After 60 seconds, further digging becomes impossible.
 - Doors leading to the Arena open.
3. Arena Phase:
 - Players independently move to the Arena.
 - The fight continues until one of the players wins.
 - After the fight ends, the frag counter is updated.
4. New Round:
 - A timer for 5 seconds starts.
 - Players reappear on the map near the triggers to start a new round.
 - To balance the game, it was decided that an identical set of items would appear in the pits.
In this project, I was fully responsible for creating this modification. To implement the excavation pit, I devised a special system in which:
A random list of items is selected from pre-created arrays of Weapon, Equip, Arrow, Filler.
A tower measuring 4x4x9 is created, and the coordinates of the created blocks are recorded in an array.
Items from the list of random items are also recorded in this array.
The standard drop of items from the block is disabled (in our case, it was a stone block).
When a player mines a block, the coordinates of the mined block are compared with the coordinates from the list, and then an item from the list is created in the world.