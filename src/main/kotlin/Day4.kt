/*
--- Day 4: Printing Department ---
You ride the escalator down to the printing department. They're clearly getting ready for Christmas; they have lots of large rolls of paper everywhere, and there's even a massive printer in the corner (to handle the really big print jobs).

Decorating here will be easy: they can make their own decorations. What you really need is a way to get further into the North Pole base while the elevators are offline.

"Actually, maybe we can help with that," one of the Elves replies when you ask for help. "We're pretty sure there's a cafeteria on the other side of the back wall. If we could break through the wall, you'd be able to keep moving. It's too bad all of our forklifts are so busy moving those big rolls of paper around."

If you can optimize the work the forklifts are doing, maybe they would have time to spare to break through the wall.

The rolls of paper (@) are arranged on a large grid; the Elves even have a helpful diagram (your puzzle input) indicating where everything is located.

For example:

..@@.@@@@.
@@@.@.@.@@
@@@@@.@.@@
@.@@@@..@.
@@.@@@@.@@
.@@@@@@@.@
.@.@.@.@@@
@.@@@.@@@@
.@@@@@@@@.
@.@.@@@.@.
The forklifts can only access a roll of paper if there are fewer than four rolls of paper in the eight adjacent positions. If you can figure out which rolls of paper the forklifts can access, they'll spend less time looking and more time breaking down the wall to the cafeteria.

In this example, there are 13 rolls of paper that can be accessed by a forklift (marked with x):

..xx.xx@x.
x@@.@.@.@@
@@@@@.x.@@
@.@@@@..@.
x@.@@@@.@x
.@@@@@@@.@
.@.@.@.@@@
x.@@@.@@@@
.@@@@@@@@.
x.x.@@@.x.
Consider your complete diagram of the paper roll locations. How many rolls of paper can be accessed by a forklift?

--- Part Two ---
Now, the Elves just need help accessing as much of the paper as they can.

Once a roll of paper can be accessed by a forklift, it can be removed. Once a roll of paper is removed, the forklifts might be able to access more rolls of paper, which they might also be able to remove. How many total rolls of paper could the Elves remove if they keep repeating this process?

Starting with the same example as above, here is one way you could remove as many rolls of paper as possible, using highlighted @ to indicate that a roll of paper is about to be removed, and using x to indicate that a roll of paper was just removed:

Initial state:
..@@.@@@@.
@@@.@.@.@@
@@@@@.@.@@
@.@@@@..@.
@@.@@@@.@@
.@@@@@@@.@
.@.@.@.@@@
@.@@@.@@@@
.@@@@@@@@.
@.@.@@@.@.

Remove 13 rolls of paper:
..xx.xx@x.
x@@.@.@.@@
@@@@@.x.@@
@.@@@@..@.
x@.@@@@.@x
.@@@@@@@.@
.@.@.@.@@@
x.@@@.@@@@
.@@@@@@@@.
x.x.@@@.x.

Remove 12 rolls of paper:
.......x..
.@@.x.x.@x
x@@@@...@@
x.@@@@..x.
.@.@@@@.x.
.x@@@@@@.x
.x.@.@.@@@
..@@@.@@@@
.x@@@@@@@.
....@@@...

Remove 7 rolls of paper:
..........
.x@.....x.
.@@@@...xx
..@@@@....
.x.@@@@...
..@@@@@@..
...@.@.@@x
..@@@.@@@@
..x@@@@@@.
....@@@...

Remove 5 rolls of paper:
..........
..x.......
.x@@@.....
..@@@@....
...@@@@...
..x@@@@@..
...@.@.@@.
..x@@.@@@x
...@@@@@@.
....@@@...

Remove 2 rolls of paper:
..........
..........
..x@@.....
..@@@@....
...@@@@...
...@@@@@..
...@.@.@@.
...@@.@@@.
...@@@@@x.
....@@@...

Remove 1 roll of paper:
..........
..........
...@@.....
..x@@@....
...@@@@...
...@@@@@..
...@.@.@@.
...@@.@@@.
...@@@@@..
....@@@...

Remove 1 roll of paper:
..........
..........
...x@.....
...@@@....
...@@@@...
...@@@@@..
...@.@.@@.
...@@.@@@.
...@@@@@..
....@@@...

Remove 1 roll of paper:
..........
..........
....x.....
...@@@....
...@@@@...
...@@@@@..
...@.@.@@.
...@@.@@@.
...@@@@@..
....@@@...

Remove 1 roll of paper:
..........
..........
..........
...x@@....
...@@@@...
...@@@@@..
...@.@.@@.
...@@.@@@.
...@@@@@..
....@@@...
Stop once no more rolls of paper are accessible by a forklift. In this example, a total of 43 rolls of paper can be removed.

Start with your original diagram. How many rolls of paper in total can be removed by the Elves and their forklifts?
 */
class Day4 {

    class Solver {
        fun hasRollInMap(map: Array<Array<Char>>, row: Int, col: Int): Boolean {
            val numRows = map.size
            val numCols = map[0].size
            if (row !in 0..<numRows || col !in 0..<numCols) {
                return false
            }
            return map[row][col] == '@'
        }

        fun countRollsAround(map: Array<Array<Char>>, row: Int, col: Int): Int {
            var count = 0
            for (dr in -1..1) {
                for (dc in -1..1) {
                    if (dr == 0 && dc == 0) continue
                    if (hasRollInMap(map, row + dr, col + dc)) {
                        count++
                    }
                }
            }
            return count
        }

        fun solvePart1(input: List<String>): Int {
            val map = input.map { it.toCharArray().toTypedArray() }.toTypedArray()
            val numRows = map.size
            val numCols = map[0].size

            return (0..<numCols).flatMap { col ->
                (0..<numRows).map { row -> Pair(row, col) }
            }.count { (row, col) -> hasRollInMap(map, row, col) && countRollsAround(map, row, col) < 4 }
        }

        fun solvePart2(input: List<String>): Int {
            val map = input.map { it.toCharArray().toTypedArray() }.toTypedArray()
            val numRows = map.size
            val numCols = map[0].size
            var totalRemoved = 0
            var removedInLastIteration: Int

            do {
                val toRemove = (0..<numCols).flatMap { col ->
                    (0..<numRows).map { row -> Pair(row, col) }
                }.filter { (row, col) -> hasRollInMap(map, row, col) && countRollsAround(map, row, col) < 4 }

                for ((row, col) in toRemove) {
                    map[row][col] = '.'
                }

                removedInLastIteration = toRemove.size

                totalRemoved += removedInLastIteration
            } while (removedInLastIteration > 0)

            return totalRemoved
        }
    }

}

fun main() {
    val input = Day4::class.java.getResourceAsStream("/day4_sample.txt")!!.bufferedReader().readLines()
    val realInput = Day4::class.java.getResourceAsStream("/day4.txt")!!.bufferedReader().readLines()
    val solver = Day4.Solver()
    println("Sample Part1")
    val part1 = solver.solvePart1(input)
    println("Part1")
    val realPart1 = solver.solvePart1(realInput)
    println("Sample Part2")
    val part2 = solver.solvePart2(input)
    println("Part2")
    val realPart2 = solver.solvePart2(realInput)
    println("Part 1: $part1")
    println("Real Part 1: $realPart1")
    println("Part 2: $part2")
    println("Real Part 2: $realPart2")
}