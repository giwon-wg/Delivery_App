package com.example.delivery_app.domain.menu.dto.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateMenuRequestDto {

	@Schema(description = "메뉴 사진", example = "잘나온 사진")
	@JsonProperty(value = "menuPicture")
	private final String menuPicture;

	@Schema(description = "메뉴 이름", example = "사이다")
	@NotBlank(message = "메뉴명을 입력해주세요")
	@Size(max = 20, message = "메뉴명은 20자 이내로 입력해주세요")
	@JsonProperty("menuName")
	private final String menuName;

	@Schema(description = "메뉴 가격", example = "2000")
	@NotNull(message = "가격을 입력해주세요")
	@JsonProperty("price")
	private final Integer price;

	@Schema(description = "메뉴 설명", example = "역시 사이다~")
	@NotBlank(message = "메뉴 설명을 입력해주세요")
	@JsonProperty("menuContent")
	private final String menuContent;

}
