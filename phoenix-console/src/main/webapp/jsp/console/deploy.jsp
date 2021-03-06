<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="http://www.dianping.com/phoenix/console"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.deploy.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.deploy.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.deploy.Model" scope="request" />

<a:layout>
	
	<c:set var="deploy" value="${model.deploy}"/>
	<div class="row-fluid">
		<div class="span4">
			<div class="page-header">
                <input type="hidden" id="deploy_id" value="${deploy.id}">
				<strong style="font-size: medium;">${deploy.domain}</strong>：
				[<font color="blue">${deploy.version}</font>, 方式：1->1->1->1, 错误：终断后续发布], 结果：[<strong><span id="deploy_status">${deploy.status}</span></strong>]
			</div>
			<div class="row-fluid">
				<div class="span12 thumbnail" style="height: 440px; overflow: auto;">
					<table class="table table-condensed nohover">
						<thead>
							<tr>
								<th width="105">Machine</th>
								<th>Progress</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="entry" items="${deploy.hosts}" varStatus="status">
								<c:set var="host" value="${entry.value}"/>
								<tr class="host_status" id="${host.ip}" data-offset="${host.offset}">
									<td>${host.ip}<i class="log-arrow icon-chevron-left<c:if test="${status.index > 0}"> hide</c:if>"></i></td>
									<td>
                                        <div class="progress ${
                                        	host.status eq 'successful' ? 'progress-success' 
	                                        	: (host.status eq 'failed' ? 'progress-danger' 
	                                        	: (host.status eq 'doing' ? 'progress-striped active' 
	                                        	: (host.status eq 'cancelled' ? 'progress-cancelled' 
	                                        	: (host.status eq 'warning' ? 'progress-warning' 
	                                        	: ''))))
	                                        }">
                                            <div class="bar" style="width: ${host.progress}%;">
                                                <div class="step" style="width: 250px;color: #000000;">${host.currentStep}</div>
                                            </div>
                                        </div>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			<div class="row" style="margin-top: 5px;">
				<p class="pull-right">
					<span class="label label-pending">pending&nbsp;&nbsp;&nbsp;</span> 
					<span class="label label-doing">doing&nbsp;&nbsp;</span> 
					<span class="label label-warning">warning</span> 
					<span class="label label-cancelled">cancelled</span> 
					<span class="label label-success">success</span> 
					<span class="label label-failed">failed&nbsp;</span>
				</p>
			</div>

			<div id="result" style="display: none"></div>
		</div>
		<div class="span8">
			<div class="row-fluid">
				<div class="page-header">
					<h4>Deployment Details</h4>
				</div>
				<c:forEach var="entry" items="${deploy.hosts}" varStatus="status">
					<c:set var="host" value="${entry.value}"/>
					<div id="log-${host.ip}" data-spy="scroll" data-offset="0" style="height: 508px; line-height: 20px; overflow: auto;"
						 class="terminal terminal-like<c:if test="${status.index > 0}"> hide</c:if>">
						<c:forEach var="segment" items="${host.segments}">
							<c:if test="${not empty segment.encodedText}">
								<div class="terminal-like">${segment.encodedText}</div>
							</c:if>
						</c:forEach>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>

	<res:useJs value="${res.js.local.deploy_js}" target="deploy-js" />
	<res:useCss value='${res.css.local.deploy_css}' target="head-css" />
	<res:jsSlot id="deploy-js" />
	<res:cssSlot id="deploy-css" />
</a:layout>