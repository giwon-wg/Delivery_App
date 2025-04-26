package com.example.delivery_app.domain.order.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.delivery_app.common.jwt.JwtTokenProvider;
import com.example.delivery_app.common.redis.service.BlackListService;
import com.example.delivery_app.domain.order.dto.response.OrderResponseDto;
import com.example.delivery_app.domain.order.entity.OrderStatus;
import com.example.delivery_app.domain.order.service.OrderService;
import com.example.delivery_app.domain.user.Auth.UserAuth;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
class OrderControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	OrderService orderService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@MockitoBean
	private BlackListService blackListService;

	@DisplayName("새로운 주문을 요청(생성)합니다.")
	@Test
	@WithMockUser(username = "wannabeing")
	void sendOrder() throws Exception {
		// given
		OrderResponseDto dto = OrderResponseDto.builder()
			.orderId(1L)
			.userId(1L)
			.menuId(1L)
			.storeId(1L)
			.status(OrderStatus.REQUESTED)
			.totalPrice(6000)
			.build();
		given(orderService.sendOrder(anyLong(), any(UserAuth.class))).willReturn(dto);

		// when & then
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/orders/{menuId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@WithMockUser(username = "wannabeing")
	@DisplayName("주문내역을 조회합니다.")
	@Test
	void getAllOrders() throws Exception {
		// given
		OrderResponseDto dto = OrderResponseDto.builder()
			.orderId(1L)
			.userId(1L)
			.menuId(1L)
			.storeId(1L)
			.status(OrderStatus.REQUESTED)
			.totalPrice(6000)
			.build();
		given(orderService.findAllOrders(any(UserAuth.class))).willReturn(List.of(dto));

		// when & then
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/orders")
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk());

	}

	@DisplayName("단일 주문을 조회합니다.")
	@Test
	@WithMockUser(username = "wannabeing")
	void getOrder() throws Exception {
		// given
		OrderResponseDto dto = OrderResponseDto.builder()
			.orderId(1L)
			.userId(1L)
			.menuId(1L)
			.storeId(1L)
			.status(OrderStatus.REQUESTED)
			.totalPrice(6000)
			.build();
		given(orderService.findById(anyLong(), any(UserAuth.class))).willReturn(dto);

		// when & then
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/orders/{orderId}", 1L)
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk());

	}

	@DisplayName("주문을 수락합니다.")
	@Test
	@WithMockUser(username = "wannabeing")
	void acceptOrder() throws Exception {
		// given
		OrderResponseDto dto = OrderResponseDto.builder()
			.orderId(1L)
			.userId(1L)
			.menuId(1L)
			.storeId(1L)
			.status(OrderStatus.ACCEPTED)
			.totalPrice(6000)
			.build();
		given(orderService.requestAdminOrder(anyLong(), eq(OrderStatus.ACCEPTED), any(UserAuth.class))).willReturn(dto);

		// when & then
		mockMvc.perform(MockMvcRequestBuilders
				.patch("/api/orders/{orderId}/accept", 1L)
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk());

	}

	@DisplayName("사용자가 배달을 받았습니다.")
	@Test
	@WithMockUser(username = "wannabeing")
	void completeOrder() throws Exception {
		// given
		OrderResponseDto dto = OrderResponseDto.builder()
			.orderId(1L)
			.userId(1L)
			.menuId(1L)
			.storeId(1L)
			.status(OrderStatus.DELIVERED)
			.totalPrice(6000)
			.build();
		given(orderService.requestUserOrder(anyLong(), eq(OrderStatus.DELIVERED), any(UserAuth.class))).willReturn(dto);

		// when & then
		mockMvc.perform(MockMvcRequestBuilders
				.patch("/api/orders/{orderId}/complete", 1L)
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk());

	}

	@DisplayName("주문을 거절합니다.")
	@Test
	@WithMockUser(username = "wannabeing")
	void rejectOrder() throws Exception {
		// given
		OrderResponseDto dto = OrderResponseDto.builder()
			.orderId(1L)
			.userId(1L)
			.menuId(1L)
			.storeId(1L)
			.status(OrderStatus.CANCELLED)
			.totalPrice(6000)
			.build();
		given(orderService.requestUserOrder(anyLong(), eq(OrderStatus.CANCELLED), any(UserAuth.class))).willReturn(dto);

		// when & then
		mockMvc.perform(MockMvcRequestBuilders
				.patch("/api/orders/{orderId}/reject", 1L)
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk());

	}
}