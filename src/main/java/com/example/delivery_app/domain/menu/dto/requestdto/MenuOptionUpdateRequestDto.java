package com.example.delivery_app.domain.menu.dto.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MenuOptionUpdateRequestDto {

	@NotBlank(message = "옵션명을 입력해주세요")
	@Size(max = 20, message = "옵션명은 20자 이내로 입력해주세요")
	@JsonProperty("optionName")
	private final String optionName;

	@NotNull(message = "가격을 입력해주세요")
	@JsonProperty("price")
	private final Integer price;

	@NotBlank(message = "옵션 설명을 입력해주세요")
	@JsonProperty("content")
	private final String content;

}
