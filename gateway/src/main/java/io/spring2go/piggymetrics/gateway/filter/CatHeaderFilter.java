package io.spring2go.piggymetrics.gateway.filter;

import org.springframework.stereotype.Component;

import com.dianping.cat.Cat;
import com.dianping.cat.Cat.Context;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import io.spring2go.cathelper.CatContext;
import io.spring2go.cathelper.CatHttpConstants;

// 借助Zuul Filter以跨进程边界方式传递CAT调用链上下文
@Component
public class CatHeaderFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
	     // 保存和传递CAT调用链上下文
	    Context ctx = new CatContext();
	    Cat.logRemoteCallClient(ctx);
		RequestContext requestContext = RequestContext.getCurrentContext();
	    requestContext.addZuulRequestHeader(CatHttpConstants.CAT_HTTP_HEADER_ROOT_MESSAGE_ID, ctx.getProperty(Cat.Context.ROOT));
	    requestContext.addZuulRequestHeader(CatHttpConstants.CAT_HTTP_HEADER_PARENT_MESSAGE_ID, ctx.getProperty(Cat.Context.PARENT));
	    requestContext.addZuulRequestHeader(CatHttpConstants.CAT_HTTP_HEADER_CHILD_MESSAGE_ID, ctx.getProperty(Cat.Context.CHILD));
	    return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 10;
	}

}
