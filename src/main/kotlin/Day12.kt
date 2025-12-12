import kotlin.time.Duration

/*
--- Day 12: Christmas Tree Farm ---
You're almost out of time, but there can't be much left to decorate. Although there are no stairs, elevators, escalators, tunnels, chutes, teleporters, firepoles, or conduits here that would take you deeper into the North Pole base, there is a ventilation duct. You jump in.

After bumping around for a few minutes, you emerge into a large, well-lit cavern full of Christmas trees!

There are a few Elves here frantically decorating before the deadline. They think they'll be able to finish most of the work, but the one thing they're worried about is the presents for all the young Elves that live here at the North Pole. It's an ancient tradition to put the presents under the trees, but the Elves are worried they won't fit.

The presents come in a few standard but very weird shapes. The shapes and the regions into which they need to fit are all measured in standard units. To be aesthetically pleasing, the presents need to be placed into the regions in a way that follows a standardized two-dimensional unit grid; you also can't stack presents.

As always, the Elves have a summary of the situation (your puzzle input) for you. First, it contains a list of the presents' shapes. Second, it contains the size of the region under each tree and a list of the number of presents of each shape that need to fit into that region. For example:

0:
###
##.
##.

1:
###
##.
.##

2:
.##
###
##.

3:
##.
###
##.

4:
###
#..
###

5:
###
.#.
###

4x4: 0 0 0 0 2 0
12x5: 1 0 1 0 2 2
12x5: 1 0 1 0 3 2
The first section lists the standard present shapes. For convenience, each shape starts with its index and a colon; then, the shape is displayed visually, where # is part of the shape and . is not.

The second section lists the regions under the trees. Each line starts with the width and length of the region; 12x5 means the region is 12 units wide and 5 units long. The rest of the line describes the presents that need to fit into that region by listing the quantity of each shape of present; 1 0 1 0 3 2 means you need to fit one present with shape index 0, no presents with shape index 1, one present with shape index 2, no presents with shape index 3, three presents with shape index 4, and two presents with shape index 5.

Presents can be rotated and flipped as necessary to make them fit in the available space, but they have to always be placed perfectly on the grid. Shapes can't overlap (that is, the # part from two different presents can't go in the same place on the grid), but they can fit together (that is, the . part in a present's shape's diagram does not block another present from occupying that space on the grid).

The Elves need to know how many of the regions can fit the presents listed. In the above example, there are six unique present shapes and three regions that need checking.

The first region is 4x4:

....
....
....
....
In it, you need to determine whether you could fit two presents that have shape index 4:

###
#..
###
After some experimentation, it turns out that you can fit both presents in this region. Here is one way to do it, using A to represent one present and B to represent the other:

AAA.
ABAB
ABAB
.BBB
The second region, 12x5: 1 0 1 0 2 2, is 12 units wide and 5 units long. In that region, you need to try to fit one present with shape index 0, one present with shape index 2, two presents with shape index 4, and two presents with shape index 5.

It turns out that these presents can all fit in this region. Here is one way to do it, again using different capital letters to represent all the required presents:

....AAAFFE.E
.BBBAAFFFEEE
DDDBAAFFCECE
DBBB....CCC.
DDD.....C.C.
The third region, 12x5: 1 0 1 0 3 2, is the same size as the previous region; the only difference is that this region needs to fit one additional present with shape index 4. Unfortunately, no matter how hard you try, there is no way to fit all of the presents into this region.

So, in this example, 2 regions can fit all of their listed presents.

Consider the regions beneath each tree and the presents the Elves would like to fit into each of them. How many of the regions can fit all of the presents listed?

--- Part Two ---
The Elves thank you profusely for the help and start rearranging the oddly-shaped presents. As you look up, you notice that a lot more Elves have arrived here at the Christmas tree farm.

In fact, many of these new arrivals look familiar: they're the Elves you helped while decorating the North Pole base. Right on schedule, each group seems to have brought a star to put atop one of the Christmas trees!

Before any of them can find a ladder, a particularly large Christmas tree suddenly flashes brightly when a large star magically appears above it! As your eyes readjust, you think you notice a portly man with a white beard disappear into the crowd.

You go look for a ladder; only 23 stars to go.
 */
class Day12 {

    data class PresentShape(val index: Int, val shape: List<String>) {
        val height = shape.size
        val width = shape[0].length

        fun rotate(): PresentShape {
            val newShape = List(width) { row ->
                StringBuilder().apply {
                    for (col in height - 1 downTo 0) {
                        append(shape[col][row])
                    }
                }.toString()
            }
            return PresentShape(index, newShape)
        }

        fun flipHorizontally(): PresentShape {
            val newShape = shape.map { it.reversed() }
            return PresentShape(index, newShape)
        }

        fun flipVertically(): PresentShape {
            val newShape = shape.reversed()
            return PresentShape(index, newShape)
        }

        fun getAllOrientations(): Set<PresentShape> {
            return (0 until 4).flatMap { nbRotates ->
                val rotated = (0 until nbRotates).fold(this) { acc, _ -> acc.rotate()  }
                sequenceOf(rotated, rotated.flipVertically(), rotated.flipHorizontally(), rotated.flipVertically().flipHorizontally())
            }.toSet()
        }
    }

    class Solver {
        fun solvePart1(input: List<String>): Int {

            val sections = input.joinToString("\n").split("\n\n").map { it.trim() }.map { it.split("\n") }

            val presentShapes = sections.dropLast(1).associate { lines ->
                val index = lines.first().removeSuffix(":").toInt()
                val shape = lines.drop(1)
                val cellCount = shape.sumOf { row -> row.count { it == '#' } }
                index to Pair(PresentShape(index, shape).getAllOrientations(), cellCount)
            }

            val regions = sections.last().map { line ->
                val parts = line.split(":")
                val sizePart = parts[0].trim()
                val countsPart = parts[1].trim()
                val (width, height) = sizePart.split("x").map { it.toInt() }
                val counts = countsPart.split(" ").map { it.toInt() }.withIndex().associate { (i, value) -> i to value }.toMutableMap()
                Triple(height, width, counts)
            }

            return regions.count { (height, width, counts) ->
                val regionArea = height * width

                // Calculate total cells needed
                val totalCellsNeeded = counts.entries.sumOf { (presentType, count) ->
                    count * presentShapes[presentType]!!.second
                }

                totalCellsNeeded <= regionArea
            }
        }

        fun solvePart2(input: List<String>): Int {
            // Implementation for Part 2
            return 0
        }
    }

}

fun main() {
    val input = Day12::class.java.getResourceAsStream("/day12_sample.txt")!!.bufferedReader().readLines()
    val realInput = Day12::class.java.getResourceAsStream("/day12.txt")!!.bufferedReader().readLines()
    val solver = Day12.Solver()
    val part1 = solver.solvePart1(input)
    println("Part 1 sample : $part1")
    val realPart1 = solver.solvePart1(realInput)
    println("Real Part 1: $realPart1")
    /*val part2 = solver.solvePart2(input)
    println("Part 2 sample: $part2")
    val realPart2 = solver.solvePart2(realInput)
    println("Real Part 2: $realPart2")*/
}
