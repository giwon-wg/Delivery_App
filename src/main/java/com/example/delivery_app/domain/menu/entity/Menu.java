package com.example.delivery_app.domain.menu.entity;

import java.util.List;

import com.example.delivery_app.common.entity.BaseEntity;
import com.example.delivery_app.domain.menu.dto.requestdto.UpdateMenuRequestDto;
import com.example.delivery_app.domain.order.entity.Order;
import com.example.delivery_app.domain.store.entity.Store;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "menu")
public class Menu extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	@Column(nullable = false)
	private String category;

	private String menuPicture;

	@Column(nullable = false)
	private String menuName;

	@Column(nullable = false)
	private Integer price;

	@Column(nullable = false)
	private String menuContent;

	@OneToOne(mappedBy = "menu")
	private Order order;

	@OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
	private List<MenuOption> menuOption;

	/**
	 * 기본값 false
	 * 삭제 시 updateStatus를 통해 true로 바뀝니다
	 */
	private boolean isDeleted;

	@Builder
	public Menu(Store store, String category, String menuPicture, String menuName, Integer price, String menuContent) {
		this.store = store;
		this.category = category;
		this.menuPicture = menuPicture;
		this.menuName = menuName;
		this.price = price;
		this.menuContent = menuContent;
	}

	/**
	 * deleteMenu 시 status 상태 변경 메서드
	 */
	public void deleteMenu() {
		this.isDeleted = true;
	}

	/**
	 * updateMenu를 위한 메서드
	 * @param dto
	 */
	public void update(UpdateMenuRequestDto dto) {
		if (dto.getMenuPicture() != null) {
			this.menuPicture = dto.getMenuPicture();
		}
		this.menuName = dto.getMenuName();
		this.price = dto.getPrice();
		this.menuContent = dto.getMenuContent();
	}
}
