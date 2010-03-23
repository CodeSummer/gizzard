package com.twitter.gizzard.nameserver

import shards._


class ShardNameServer[S <: Shard](nameServer: CachingNameServer, shardRepository: ShardRepository[S], serviceId: Int) {
  def findShardById(shardId: Int, weight: Int): S = {
    val shardInfo = nameServer.getShardInfo(shardId)
    val children = nameServer.getChildren(shardId).map { childInfo =>
      findShardById(childInfo.shardId, childInfo.weight)
    }
    shardRepository.find(shardInfo, weight, children)
  }

  def findShardById(shardId: Int): S = findShardById(shardId, 1)

  def findCurrentForwarding(tableId: Int, sourceId: Long) = nameServer.findCurrentForwarding(serviceId, tableId, sourceId)
}
