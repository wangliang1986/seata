package io.seata.core.rpc.hook;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.seata.common.loader.EnhancedServiceLoader;
import io.seata.core.protocol.RpcMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Server hook trigger
 *
 * @author wang.liang
 * @since 1.5.0
 */
public class ServerHookTrigger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHookTrigger.class);

    private ServerHookTrigger() {
    }

    private static final List<ServerHook> SERVER_HOOKS = EnhancedServiceLoader.loadAll(ServerHook.class);


    //region ServerOnRequestProcessor.process()

    public static void triggerBeforeProcessRequest(ChannelHandlerContext ctx, RpcMessage rpcMessage) {
        for (ServerHook hook : SERVER_HOOKS) {
            try {
                hook.beforeProcessRequest(ctx, rpcMessage);
            } catch (Throwable th) {
                LOGGER.error("do server hook `{}.beforeProcessRequest` error!", hook.getClass().getSimpleName(), th);
            }
        }
    }

    public static void triggerAfterProcessRequestFailed(ChannelHandlerContext ctx, RpcMessage rpcMessage, Throwable t) {
        for (ServerHook hook : SERVER_HOOKS) {
            try {
                hook.afterProcessRequestFailed(ctx, rpcMessage, t);
            } catch (Throwable th) {
                LOGGER.error("do server hook `{}.afterProcessRequestFailed` error!", hook.getClass().getSimpleName(), th);
            }
        }
    }

    public static void triggerAfterProcessRequest(ChannelHandlerContext ctx, RpcMessage rpcMessage, Throwable t) {
        for (ServerHook hook : SERVER_HOOKS) {
            try {
                hook.afterProcessRequest(ctx, rpcMessage, t);
            } catch (Throwable th) {
                LOGGER.error("do server hook `{}.afterProcessRequest` error!", hook.getClass().getSimpleName(), th);
            }
        }
    }

    //endregion

}
