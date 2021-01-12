package io.seata.core.rpc.hook;

import io.netty.channel.ChannelHandlerContext;
import io.seata.core.protocol.RpcMessage;

/**
 * The type Server hook
 *
 * @author wang.liang
 * @since 1.5.0
 */
public interface ServerHook {

    //region ServerOnRequestProcessor.process()

    default void beforeProcessRequest(ChannelHandlerContext ctx, RpcMessage rpcMessage) {
    }

    default void afterProcessRequestFailed(ChannelHandlerContext ctx, RpcMessage rpcMessage, Throwable t) {
    }

    default void afterProcessRequest(ChannelHandlerContext ctx, RpcMessage rpcMessage, Throwable t) {
    }

    //endregion
}
