package com.example.delivery_app.common.aop;

import java.time.LocalDateTime;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.example.delivery_app.domain.order.dto.response.OrderResponseDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class OrderRequestLoggingAspect {

	@Pointcut("execution(* com.example.delivery_app.domain.order.service.OrderService.sendOrder(..))")
	public void orderRequestPointcut() {
	}

	@Pointcut("execution(* com.example.delivery_app.domain.order.service.OrderService.request*Order(..))")
	public void orderStatusUpdatedPointcut() {
	}

	@AfterReturning(pointcut = "orderRequestPointcut()", returning = "response")
	public void logAfterOrderRequest(Object response) {
		logAfterOrder(response);
	}

	@AfterReturning(pointcut = "orderStatusUpdatedPointcut()", returning = "response")
	public void logAfterOrderUpdateStatus(Object response) {
		logAfterOrder(response);
	}

	/**
	 * 주문 요청에 대한 로그를 출력하는 메서드
	 * @param response 응답 객체
	 */
	private void logAfterOrder(Object response) {
		if (response instanceof OrderResponseDto dto) {
			log.info("🕒 주문 처리 시각: {}, 주문 ID: {}, 가게 ID: {}, 주문 상태: {}",
				LocalDateTime.now(),
				dto.getOrderId(),
				dto.getStoreId(),
				dto.getStatus());
		} else {
			log.warn("주문 응답 객체 형식이 잘못되었습니다.: {}", response.getClass().getSimpleName());
		}
	}
}