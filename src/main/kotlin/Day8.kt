import java.util.UUID
import kotlin.math.sqrt

/*
--- Day 8: Playground ---
Equipped with a new understanding of teleporter maintenance, you confidently step onto the repaired teleporter pad.

You rematerialize on an unfamiliar teleporter pad and find yourself in a vast underground space which contains a giant playground!

Across the playground, a group of Elves are working on setting up an ambitious Christmas decoration project. Through careful rigging, they have suspended a large number of small electrical junction boxes.

Their plan is to connect the junction boxes with long strings of lights. Most of the junction boxes don't provide electricity; however, when two junction boxes are connected by a string of lights, electricity can pass between those two junction boxes.

The Elves are trying to figure out which junction boxes to connect so that electricity can reach every junction box. They even have a list of all of the junction boxes' positions in 3D space (your puzzle input).

For example:

162,817,812
57,618,57
906,360,560
592,479,940
352,342,300
466,668,158
542,29,236
431,825,988
739,650,466
52,470,668
216,146,977
819,987,18
117,168,530
805,96,715
346,949,466
970,615,88
941,993,340
862,61,35
984,92,344
425,690,689
This list describes the position of 20 junction boxes, one per line. Each position is given as X,Y,Z coordinates. So, the first junction box in the list is at X=162, Y=817, Z=812.

To save on string lights, the Elves would like to focus on connecting pairs of junction boxes that are as close together as possible according to straight-line distance. In this example, the two junction boxes which are closest together are 162,817,812 and 425,690,689.

By connecting these two junction boxes together, because electricity can flow between them, they become part of the same circuit. After connecting them, there is a single circuit which contains two junction boxes, and the remaining 18 junction boxes remain in their own individual circuits.

Now, the two junction boxes which are closest together but aren't already directly connected are 162,817,812 and 431,825,988. After connecting them, since 162,817,812 is already connected to another junction box, there is now a single circuit which contains three junction boxes and an additional 17 circuits which contain one junction box each.

The next two junction boxes to connect are 906,360,560 and 805,96,715. After connecting them, there is a circuit containing 3 junction boxes, a circuit containing 2 junction boxes, and 15 circuits which contain one junction box each.

The next two junction boxes are 431,825,988 and 425,690,689. Because these two junction boxes were already in the same circuit, nothing happens!

This process continues for a while, and the Elves are concerned that they don't have enough extension cables for all these circuits. They would like to know how big the circuits will be.

After making the ten shortest connections, there are 11 circuits: one circuit which contains 5 junction boxes, one circuit which contains 4 junction boxes, two circuits which contain 2 junction boxes each, and seven circuits which each contain a single junction box. Multiplying together the sizes of the three largest circuits (5, 4, and one of the circuits of size 2) produces 40.

Your list contains many junction boxes; connect together the 1000 pairs of junction boxes which are closest together. Afterward, what do you get if you multiply together the sizes of the three largest circuits?

--- Part Two ---
The Elves were right; they definitely don't have enough extension cables. You'll need to keep connecting junction boxes together until they're all in one large circuit.

Continuing the above example, the first connection which causes all of the junction boxes to form a single circuit is between the junction boxes at 216,146,977 and 117,168,530. The Elves need to know how far those junction boxes are from the wall so they can pick the right extension cable; multiplying the X coordinates of those two junction boxes (216 and 117) produces 25272.

Continue connecting the closest unconnected pairs of junction boxes together until they're all in the same circuit. What do you get if you multiply together the X coordinates of the last two junction boxes you need to connect?
 */
class Day8 {

    class Solver {

        data class Point3D(val x: Int, val y: Int, val z: Int) {
            fun distanceTo(other: Point3D): Double {
                val dx = (x - other.x).toDouble()
                val dy = (y - other.y).toDouble()
                val dz = (z - other.z).toDouble()
                return sqrt(dx * dx + dy * dy + dz * dz)
            }
        }

        data class Circuit(val id: String, val points: List<Point3D>) {
            fun size(): Int = points.size

            fun distanceTo(other: Circuit): Double {
                return points.minOf { p1 ->
                    other.points.minOf { p2 ->
                        p1.distanceTo(p2)
                    }
                }
            }
        }

        data class PointWithFlag(val point: Point3D, val circuitId: String?) {
            fun distanceTo(other: PointWithFlag): Double {
                return point.distanceTo(other.point)
            }

            val isInCircuit: Boolean
                get() = circuitId != null
        }

        fun solvePart1(input: List<String>, iterations: Int): Int {
            var points = input.map {
                val (x, y, z) = it.split(",").map { coord -> coord.toInt() }
                Point3D(x, y, z)
            }

            var circuits = listOf<Circuit>()

            var lastDistance = 0.0

            for (i in 0 until iterations) {
                val allPoints = points.map { PointWithFlag(it, null) } + circuits.flatMap { it.points.map { p -> PointWithFlag(p, it.id) } }

                var toConnect = allPoints.map { p ->
                    val otherPoint = allPoints.filter { o -> o != p }.minBy { o ->
                        val distance = o.distanceTo(p)
                        if (distance <= lastDistance) {
                            Double.MAX_VALUE
                        } else {
                            distance
                        }
                    }
                    p to otherPoint
                }.minBy { (p, o) ->
                    val distance = p.distanceTo(o)
                    if (distance <= lastDistance) {
                        Double.MAX_VALUE
                    } else {
                        distance
                    }
                }

                if (toConnect.first.isInCircuit && !toConnect.second.isInCircuit) {
                    // swap to always have first not in circuit
                    toConnect = toConnect.second to toConnect.first
                }

                lastDistance = toConnect.first.distanceTo(toConnect.second)

                if (!toConnect.first.isInCircuit) {
                    points = points.filter { it != toConnect.first.point && it != toConnect.second.point }

                    if (toConnect.second.isInCircuit) {
                        circuits = circuits.map { circuit ->
                            if (circuit.id == toConnect.second.circuitId) {
                                circuit.copy(
                                    points = circuit.points + toConnect.first.point
                                )
                            } else {
                                circuit
                            }
                        }
                    } else {
                        circuits =
                            circuits + Circuit(
                                UUID.randomUUID().toString(),
                                listOf(toConnect.first.point, toConnect.second.point)
                            )
                    }
                } else if (toConnect.first.circuitId != toConnect.second.circuitId) {
                    // both points are in circuits
                    circuits = circuits.filter { it.id != toConnect.first.circuitId && it.id != toConnect.second.circuitId} + Circuit(
                        UUID.randomUUID().toString(),
                        circuits.filter { it.id == toConnect.first.circuitId || it.id == toConnect.second.circuitId }
                            .flatMap { it.points }
                    )
                } else {
                    // both points are in the same circuit, do nothing

                }

            }

            val top3 = circuits.map { it.size() }.sortedDescending().take(3)
            return top3.reduce { acc, i -> acc * i }
        }

        fun solvePart2(input: List<String>): Int {
            var points = input.map {
                val (x, y, z) = it.split(",").map { coord -> coord.toInt() }
                Point3D(x, y, z)
            }

            var circuits = listOf<Circuit>()

            var lastDistance = 0.0

            var lastConnection = Point3D(0, 0, 0) to Point3D(0, 0, 0)

            while (points.isNotEmpty() || circuits.size != 1) {
                val allPoints = points.map { PointWithFlag(it, null) } + circuits.flatMap { it.points.map { p -> PointWithFlag(p, it.id) } }

                var toConnect = allPoints.map { p ->
                    val otherPoint = allPoints.filter { o -> o != p }.minBy { o ->
                        val distance = o.distanceTo(p)
                        if (distance <= lastDistance) {
                            Double.MAX_VALUE
                        } else {
                            distance
                        }
                    }
                    p to otherPoint
                }.minBy { (p, o) ->
                    val distance = p.distanceTo(o)
                    if (distance <= lastDistance) {
                        Double.MAX_VALUE
                    } else {
                        distance
                    }
                }

                if (toConnect.first.isInCircuit && !toConnect.second.isInCircuit) {
                    // swap to always have first not in circuit
                    toConnect = toConnect.second to toConnect.first
                }

                lastDistance = toConnect.first.distanceTo(toConnect.second)

                if (!toConnect.first.isInCircuit) {
                    points = points.filter { it != toConnect.first.point && it != toConnect.second.point }

                    if (toConnect.second.isInCircuit) {
                        circuits = circuits.map { circuit ->
                            if (circuit.id == toConnect.second.circuitId) {
                                circuit.copy(
                                    points = circuit.points + toConnect.first.point
                                )
                            } else {
                                circuit
                            }
                        }
                    } else {
                        circuits =
                            circuits + Circuit(
                                UUID.randomUUID().toString(),
                                listOf(toConnect.first.point, toConnect.second.point)
                            )
                    }
                } else if (toConnect.first.circuitId != toConnect.second.circuitId) {
                    // both points are in circuits
                    circuits = circuits.filter { it.id != toConnect.first.circuitId && it.id != toConnect.second.circuitId} + Circuit(
                        UUID.randomUUID().toString(),
                        circuits.filter { it.id == toConnect.first.circuitId || it.id == toConnect.second.circuitId }
                            .flatMap { it.points }
                    )
                } else {
                    // both points are in the same circuit, do nothing

                }

                lastConnection = toConnect.first.point to toConnect.second.point

                println("Circuits: ${circuits.size}, Points: ${points.size}")
            }

            return lastConnection.first.x * lastConnection.second.x
        }
    }

}

fun main() {
    val input = Day8::class.java.getResourceAsStream("/day8_sample.txt")!!.bufferedReader().readLines()
    val realInput = Day8::class.java.getResourceAsStream("/day8.txt")!!.bufferedReader().readLines()
    val solver = Day8.Solver()
    println("Sample Part1")
    //val part1 = solver.solvePart1(input, 10)
    println("Part1")
    //val realPart1 = solver.solvePart1(realInput, 1000)
    println("Sample Part2")
    val part2 = solver.solvePart2(input)
    println("Part2")
    val realPart2 = solver.solvePart2(realInput)
    //println("Part 1: $part1")
    //println("Real Part 1: $realPart1")
    println("Part 2: $part2")
    println("Real Part 2: $realPart2")
}
