package com.example.delivery_app.domain.menu.entity;

import com.example.delivery_app.common.entity.BaseEntity;
import com.example.delivery_app.domain.menu.dto.requestdto.MenuRequestDto;
import com.example.delivery_app.domain.menu.dto.requestdto.UpdateMenuRequestDto;
import com.example.delivery_app.domain.order.entity.Order;
import com.example.delivery_app.domain.store.entity.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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

	private boolean status;

	public Menu(Store store, MenuRequestDto dto) {
		this.store = store;
		this.category = dto.getCategory();
		this.menuPicture = dto.getMenuPicture();
		this.menuName = dto.getMenuName();
		this.price = dto.getPrice();
		this.menuContent = dto.getMenuContent();
	}

	public void updateStatus() {
		this.status = false;
	}

	/**
	 * updateMenu를 위한 메서드
	 * 근데 여기서 menuPicture 같은게 null로 들어오면 null로 바뀌지 않나..?
	 * @param dto
	 */
	public void update(UpdateMenuRequestDto dto) {
		this.menuPicture = dto.getMenuPicture();
		this.menuName = dto.getMenuName();
		this.price = dto.getPrice();
		this.menuContent = dto.getMenuContent();
	}
}
