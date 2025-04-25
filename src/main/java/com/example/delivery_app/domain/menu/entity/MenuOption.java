package com.example.delivery_app.domain.menu.entity;

import com.example.delivery_app.common.entity.BaseEntity;
import com.example.delivery_app.domain.menu.dto.requestdto.MenuOptionUpdateRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "menuoption")
public class MenuOption extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String optionName;

	@Column(nullable = false)
	private Integer price;

	@Column(nullable = false)
	private String content;

	/**
	 * 기본값 false
	 * 삭제 시 deleteMenuOption를 통해 true로 바뀝니다
	 */
	private boolean isDeleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;

	@Builder
	public MenuOption(String optionName, Integer price, String content, boolean isDeleted, Menu menu) {
		this.optionName = optionName;
		this.price = price;
		this.content = content;
		this.isDeleted = isDeleted;
		this.menu = menu;
	}

	/**
	 * updateMenuOption을 위한 메서드
	 * @param dto
	 */
	public void updateMenuOption(MenuOptionUpdateRequestDto dto) {
		this.optionName = dto.getOptionName();
		this.price = dto.getPrice();
		this.content = dto.getContent();
	}

	/**
	 * deleteMenuOption 시 status 상태 변경 메서드
	 */
	public void deleteMenuOption() {
		this.isDeleted = true;
	}
}
