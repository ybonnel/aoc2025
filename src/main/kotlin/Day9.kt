/*
--- Day 9: Movie Theater ---
You slide down the firepole in the corner of the playground and land in the North Pole base movie theater!

The movie theater has a big tile floor with an interesting pattern. Elves here are redecorating the theater by switching out some of the square tiles in the big grid they form. Some of the tiles are red; the Elves would like to find the largest rectangle that uses red tiles for two of its opposite corners. They even have a list of where the red tiles are located in the grid (your puzzle input).

For example:

7,1
11,1
11,7
9,7
9,5
2,5
2,3
7,3
Showing red tiles as # and other tiles as ., the above arrangement of red tiles would look like this:

..............
.......#...#..
..............
..#....#......
..............
..#......#....
..............
.........#.#..
..............
You can choose any two red tiles as the opposite corners of your rectangle; your goal is to find the largest rectangle possible.

For example, you could make a rectangle (shown as O) with an area of 24 between 2,5 and 9,7:

..............
.......#...#..
..............
..#....#......
..............
..OOOOOOOO....
..OOOOOOOO....
..OOOOOOOO.#..
..............
Or, you could make a rectangle with area 35 between 7,1 and 11,7:

..............
.......OOOOO..
.......OOOOO..
..#....OOOOO..
.......OOOOO..
..#....OOOOO..
.......OOOOO..
.......OOOOO..
..............
You could even make a thin rectangle with an area of only 6 between 7,3 and 2,3:

..............
.......#...#..
..............
..OOOOOO......
..............
..#......#....
..............
.........#.#..
..............
Ultimately, the largest rectangle you can make in this example has area 50. One way to do this is between 2,5 and 11,1:

..............
..OOOOOOOOOO..
..OOOOOOOOOO..
..OOOOOOOOOO..
..OOOOOOOOOO..
..OOOOOOOOOO..
..............
.........#.#..
..............
Using two red tiles as opposite corners, what is the largest area of any rectangle you can make?

--- Part Two ---
The Elves just remembered: they can only switch out tiles that are red or green. So, your rectangle can only include red or green tiles.

In your list, every red tile is connected to the red tile before and after it by a straight line of green tiles. The list wraps, so the first red tile is also connected to the last red tile. Tiles that are adjacent in your list will always be on either the same row or the same column.

Using the same example as before, the tiles marked X would be green:

..............
.......#XXX#..
.......X...X..
..#XXXX#...X..
..X........X..
..#XXXXXX#.X..
.........X.X..
.........#X#..
..............
In addition, all of the tiles inside this loop of red and green tiles are also green. So, in this example, these are the green tiles:

..............
.......#XXX#..
.......XXXXX..
..#XXXX#XXXX..
..XXXXXXXXXX..
..#XXXXXX#XX..
.........XXX..
.........#X#..
..............
The remaining tiles are never red nor green.

The rectangle you choose still must have red tiles in opposite corners, but any other tiles it includes must now be red or green. This significantly limits your options.

For example, you could make a rectangle out of red and green tiles with an area of 15 between 7,3 and 11,1:

..............
.......OOOOO..
.......OOOOO..
..#XXXXOOOOO..
..XXXXXXXXXX..
..#XXXXXX#XX..
.........XXX..
.........#X#..
..............
Or, you could make a thin rectangle with an area of 3 between 9,7 and 9,5:

..............
.......#XXX#..
.......XXXXX..
..#XXXX#XXXX..
..XXXXXXXXXX..
..#XXXXXXOXX..
.........OXX..
.........OX#..
..............
The largest rectangle you can make in this example using only red and green tiles has area 24. One way to do this is between 9,5 and 2,3:

..............
.......#XXX#..
.......XXXXX..
..OOOOOOOOXX..
..OOOOOOOOXX..
..OOOOOOOOXX..
.........XXX..
.........#X#..
..............
Using two red tiles as opposite corners, what is the largest area of any rectangle you can make using only red and green tiles?
 */
class Day9 {

    data class Point(val x: Int, val y: Int)

    data class Rectangle(val p1: Point, val p2: Point) {
        val area: Long
            get() = kotlin.math.abs(p2.x - p1.x).plus(1).toLong() * kotlin.math.abs(p2.y - p1.y).plus(1).toLong()
    }

    data class LineSegment(val start: Point, val end: Point) {
        fun isVertical(): Boolean = start.x == end.x
        fun isHorizontal(): Boolean = start.y == end.y

        fun xOfVertical(): Int {
            if (!isVertical()) throw IllegalStateException("Not a vertical line")
            return start.x
        }

        fun yOfVertical(): IntRange {
            if (!isVertical()) throw IllegalStateException("Not a horizontal line")
            return minOf(start.y, end.y)..maxOf(start.y, end.y)
        }
    }


    class Solver {
        fun solvePart1(input: List<String>): Long {
            val redTiles = input.map { line ->
                val (x, y) = line.split(",").map { it.toInt() }
                Point(x, y)
            }.toSet()

            val biggestArea = redTiles.map { p1 ->
                redTiles.filter { p2 -> p1 != p2 }
                    .map { p2 -> Rectangle(p1, p2) }
                    .maxBy { it.area }
            }.maxBy { it.area }

            return biggestArea.area

        }

        fun solvePart2(input: List<String>): Long {
            val redTiles = input.map { line ->
                val (x, y) = line.split(",").map { it.toInt() }
                Point(x, y)
            }.toList()

            println("Found ${redTiles.size} red tiles")

            // Get all unique X and Y coordinates from red tiles (compressed coordinate space)
            val xs = redTiles.map { it.x }.distinct().sorted()
            val ys = redTiles.map { it.y }.distinct().sorted()

            // Create index mappings
            val xIndex = xs.withIndex().associate { it.value to it.index }
            val yIndex = ys.withIndex().associate { it.value to it.index }

            val widthXs = xs.size
            val heightYs = ys.size

            println("Compressed grid: ${widthXs}x${heightYs}")

            // Mark boundary tiles (perimeter of the loop)
            val boundary = Array(widthXs) { BooleanArray(heightYs) }

            println("Building boundary...")
            for (i in redTiles.indices) {
                val a = redTiles[i]
                val b = redTiles[(i + 1) % redTiles.size]

                val ax = xIndex[a.x]!!
                val ay = yIndex[a.y]!!
                val bx = xIndex[b.x]!!
                val by = yIndex[b.y]!!

                if (ax == bx) {
                    // Vertical line
                    val minY = minOf(ay, by)
                    val maxY = maxOf(ay, by)
                    for (y in minY..maxY) {
                        boundary[ax][y] = true
                    }
                } else if (ay == by) {
                    // Horizontal line
                    val minX = minOf(ax, bx)
                    val maxX = maxOf(ax, bx)
                    for (x in minX..maxX) {
                        boundary[x][ay] = true
                    }
                }
            }

            // Flood fill from outside to mark exterior tiles
            val outside = Array(widthXs) { BooleanArray(heightYs) }
            val queue = ArrayDeque<Point>()

            println("Starting flood fill from edges...")
            // Add all edge points that aren't on boundary
            for (x in 0 until widthXs) {
                if (!boundary[x][0]) {
                    queue.add(Point(x, 0))
                    outside[x][0] = true
                }
                if (!boundary[x][heightYs - 1]) {
                    queue.add(Point(x, heightYs - 1))
                    outside[x][heightYs - 1] = true
                }
            }
            for (y in 0 until heightYs) {
                if (!boundary[0][y]) {
                    queue.add(Point(0, y))
                    outside[0][y] = true
                }
                if (!boundary[widthXs - 1][y]) {
                    queue.add(Point(widthXs - 1, y))
                    outside[widthXs - 1][y] = true
                }
            }

            // Flood fill
            while (queue.isNotEmpty()) {
                val p = queue.removeFirst()

                // Check all 4 neighbors
                for (neighbor in listOf(
                    Point(p.x, p.y - 1), // up
                    Point(p.x, p.y + 1), // down
                    Point(p.x + 1, p.y), // right
                    Point(p.x - 1, p.y)  // left
                )) {
                    if (neighbor.x in 0 until widthXs &&
                        neighbor.y in 0 until heightYs &&
                        !boundary[neighbor.x][neighbor.y] &&
                        !outside[neighbor.x][neighbor.y]
                    ) {
                        outside[neighbor.x][neighbor.y] = true
                        queue.add(neighbor)
                    }
                }
            }

            println("Flood fill complete")

            // Mark allowed tiles (boundary or interior)
            val allowed = Array(widthXs) { BooleanArray(heightYs) }
            for (x in 0 until widthXs) {
                for (y in 0 until heightYs) {
                    if (boundary[x][y] || !outside[x][y]) {
                        allowed[x][y] = true
                    }
                }
            }

            // Find largest rectangle with red corners containing only allowed tiles
            var best = 0L

            println("Testing ${redTiles.size * (redTiles.size - 1) / 2} rectangle pairs...")
            var tested = 0

            for (i in redTiles.indices) {
                for (j in i + 1 until redTiles.size) {
                    tested++
                    if (tested % 1000 == 0) {
                        println("  Tested $tested pairs, current max area: $best")
                    }

                    val a = redTiles[i]
                    val b = redTiles[j]

                    val ax = xIndex[a.x]!!
                    val ay = yIndex[a.y]!!
                    val bx = xIndex[b.x]!!
                    val by = yIndex[b.y]!!

                    // Check if all points in rectangle are allowed
                    val minX = minOf(ax, bx)
                    val maxX = maxOf(ax, bx)
                    val minY = minOf(ay, by)
                    val maxY = maxOf(ay, by)

                    var ok = true
                    outer@ for (x in minX..maxX) {
                        for (y in minY..maxY) {
                            if (!allowed[x][y]) {
                                ok = false
                                break@outer
                            }
                        }
                    }

                    if (ok) {
                        val width = (maxOf(a.x, b.x) - minOf(a.x, b.x) + 1).toLong()
                        val height = (maxOf(a.y, b.y) - minOf(a.y, b.y) + 1).toLong()
                        best = maxOf(best, width * height)
                    }
                }
            }

            println("Final max area: $best")
            return best
        }
    }

}

fun main() {
    val input = Day9::class.java.getResourceAsStream("/day9_sample.txt")!!.bufferedReader().readLines()
    val realInput = Day9::class.java.getResourceAsStream("/day9.txt")!!.bufferedReader().readLines()
    val solver = Day9.Solver()
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
