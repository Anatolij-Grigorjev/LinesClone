package com.tiem625.lines.stages

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultConnection
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.tiem625.lines.actors.TileBallGroup
import com.tiem625.lines.toIndex

typealias GraphArray = com.badlogic.gdx.utils.Array<Connection<TileBallGroup>>
class IndexedGridGraph(val numRows: Int, val numCols: Int, val grid: Array<Array<TileBallGroup>>):
        IndexedGraph<TileBallGroup> {


    private val nodesTotal: Int = numRows * numCols

    private fun generateNearestConnections(tileBallGroup: TileBallGroup): GraphArray {

        //at most this will have 4 elements, so avoid resizing
        val connections = GraphArray(9)

        val pos = tileBallGroup.gridPos
        //connection left
        if (pos.first > 0) {
            connections.add(DefaultConnection<TileBallGroup>(
                    tileBallGroup,
                    grid[pos.first - 1][pos.second]
            ))
        }
        //connection right
        if (pos.first < (numRows - 1)) {
            connections.add(DefaultConnection<TileBallGroup>(
                    tileBallGroup,
                    grid[pos.first + 1][pos.second]
            ))
        }
        //connection below
        if(pos.second > 0) {
            connections.add(DefaultConnection<TileBallGroup>(
                    tileBallGroup,
                    grid[pos.first][pos.second - 1]
            ))
        }
        //connection above
        if (pos.second < (numCols - 1)) {
            connections.add(DefaultConnection<TileBallGroup>(
                    tileBallGroup,
                    grid[pos.first][pos.second + 1]
            ))
        }

        return connections
    }

    private val connectionsMap = grid.flatten().associate { tileBallGroup ->

        Pair(
                tileBallGroup,
                //generate tile ball group connections
                generateNearestConnections(tileBallGroup)
        )
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

}