package com.tiem625.lines.stages

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultConnection
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.actors.TileBallGroup
import com.tiem625.lines.toIndex

typealias GraphArray = com.badlogic.gdx.utils.Array<Connection<TileBallGroup>>
class IndexedGridGraph(val numRows: Int, val numCols: Int, val grid: Array<Array<TileBallGroup>>):
        IndexedGraph<TileBallGroup> {

    class DescriptiveDefaultConnection(from: TileBallGroup, to: TileBallGroup):
            DefaultConnection<TileBallGroup>(from, to) {
        override fun toString(): String {
            return "${fromNode.gridPos} -> ${toNode.gridPos}"
        }
    }


    private val nodesTotal: Int = numRows * numCols

    private fun generateNearestConnections(tileBallGroup: TileBallGroup): GraphArray {

        //at most this will have 4 elements, so avoid resizing
        val connections = GraphArray(9)

        val pos = tileBallGroup.gridPos

        //connection left
        if (pos.first > 0) {
            val neighbour = grid[pos.first - 1][pos.second]
            if (neighbour.ball == null || tileBallGroup.ball == null) {
                connections.add(DescriptiveDefaultConnection(
                        tileBallGroup,
                        neighbour
                ))
            }
        }
        //connection right
        if (pos.first < (numRows - 1)) {
            val neighbour = grid[pos.first + 1][pos.second]
            if (neighbour.ball == null || tileBallGroup.ball == null) {
                connections.add(DescriptiveDefaultConnection(
                        tileBallGroup,
                        neighbour
                ))
            }
        }
        //connection below
        if(pos.second > 0) {
            val neighbour = grid[pos.first][pos.second - 1]
            if (neighbour.ball == null || tileBallGroup.ball == null) {
                connections.add(DescriptiveDefaultConnection(
                        tileBallGroup,
                        neighbour
                ))
            }
        }
        //connection above
        if (pos.second < (numCols - 1)) {
            val neighbour = grid[pos.first][pos.second + 1]
            if (neighbour.ball == null || tileBallGroup.ball == null) {
                connections.add(DescriptiveDefaultConnection(
                        tileBallGroup,
                        neighbour
                ))
            }
        }

        return connections
    }

    var connectionsMap = generateNewConnectionsMap()

    private fun generateNewConnectionsMap(): Map<TileBallGroup, GraphArray> {
        return grid.flatten().associate { tileBallGroup ->

            Pair(
                    tileBallGroup,
                    //generate tile ball group connections
                    generateNearestConnections(tileBallGroup)
            )
        }
    }

    val emptyArray: GraphArray = GraphArray()

    override fun getConnections(fromNode: TileBallGroup?): GraphArray {

        return fromNode?.let {
            connectionsMap[it] ?: emptyArray
        } ?: emptyArray
    }

    override fun getIndex(node: TileBallGroup?): Int {
        return node?.gridPos?.toIndex(numCols)?: -1
    }

    override fun getNodeCount(): Int = nodesTotal

    /**
     * Force graph to generate a new connections map,
     * should be done after changes in ball graph topology
     */
    fun invalidateConnections() {
        println("invalidating connections...")
        val now = System.currentTimeMillis()
        connectionsMap = generateNewConnectionsMap()
        println("Regen graph connections took ${System.currentTimeMillis() - now}ms, created ${connectionsMap.size} connections")
//        dumpConnections()
    }

    fun dumpConnections() {

        connectionsMap.forEach {group, array->

            println("${group.gridPos}: $array")

        }
    }

}