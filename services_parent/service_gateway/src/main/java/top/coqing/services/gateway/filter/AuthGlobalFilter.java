package top.coqing.services.gateway.filter;


import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.coqing.services.common.JwtHelper.JwtHelper;
import top.coqing.services.common.result.Result;
import top.coqing.services.common.result.StateCode;
import top.coqing.services.model.user.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * @Description: 全局Filter，统一处理登录权限与外部不允许访问的服务
 * @Author: coqing
 * @Date: 2022/9/2 12:45
 **/
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
//        System.out.println(path+"-------------------------------------");

        //内部服务接口，不允许外部访问
//        if(antPathMatcher.match("/**/inner/**", path)) {
//            ServerHttpResponse response = exchange.getResponse();
//            return out(response, ResultCodeEnum.PERMISSION);
//        }


        // 需要登录的接口拦截
        if(antPathMatcher.match("/api/**/auth/**", path)) {
            User user = getUser(request);
            if(null==user) {
                ServerHttpResponse response = exchange.getResponse();
                return out(response, StateCode.LOGIN_AUTH);
            }
            request.mutate()
                    .headers(httpHeaders -> httpHeaders.add("userId",String.valueOf(user.getId()) ))
                    .build();
        }

        // 管理员的接口拦截
        if(antPathMatcher.match("/api/**/admin/**", path)) {
            User user = getUser(request);
            if(null==user) {
                ServerHttpResponse response = exchange.getResponse();
                return out(response, StateCode.LOGIN_AUTH);
            }
            if(null==user.getId()||1!=user.getRole()){
                ServerHttpResponse response = exchange.getResponse();
                return out(response, StateCode.PERMISSION);
            }
            request.mutate()
                    .headers(httpHeaders -> httpHeaders.add("userId",String.valueOf(user.getId()) ))
                    .build();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * api接口鉴权失败返回数据
     * @param response
     * @return
     */
    private Mono<Void> out(ServerHttpResponse response, StateCode stateCode) {
        Result result = Result.build(null, stateCode);
        byte[] bits = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 根据token获取用户
     * @param request
     * @return
     */
    private User getUser(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst("token");
        if(!StringUtils.isEmpty(token)) {
            return JwtHelper.getUserFromToken(token);
        }
        return null;
    }


    /**
     * 根据token获取用户id
     * @param request
     * @return
     */
    private Long getUserId(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst("token");
        if(!StringUtils.isEmpty(token)) {
            return JwtHelper.getUserId(token);
        }
        return null;
    }
}
