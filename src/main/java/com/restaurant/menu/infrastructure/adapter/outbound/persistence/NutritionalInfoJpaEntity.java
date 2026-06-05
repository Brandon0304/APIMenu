package com.restaurant.menu.infrastructure.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "nutritional_info", schema = "menu")
public class NutritionalInfoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "menu_item_id", nullable = false, unique = true)
    private UUID menuItemId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", insertable = false, updatable = false)
    private MenuItemJpaEntity menuItem;

    private Integer calories;

    @Column(name = "protein_grams", precision = 7, scale = 2)
    private BigDecimal proteinGrams;

    @Column(name = "carbs_grams", precision = 7, scale = 2)
    private BigDecimal carbsGrams;

    @Column(name = "fat_grams", precision = 7, scale = 2)
    private BigDecimal fatGrams;

    @Column(name = "fiber_grams", precision = 7, scale = 2)
    private BigDecimal fiberGrams;

    @Column(name = "sodium_mg")
    private Integer sodiumMg;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public NutritionalInfoJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getMenuItemId() { return menuItemId; }
    public void setMenuItemId(UUID menuItemId) { this.menuItemId = menuItemId; }
    public MenuItemJpaEntity getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItemJpaEntity menuItem) { this.menuItem = menuItem; }
    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }
    public BigDecimal getProteinGrams() { return proteinGrams; }
    public void setProteinGrams(BigDecimal proteinGrams) { this.proteinGrams = proteinGrams; }
    public BigDecimal getCarbsGrams() { return carbsGrams; }
    public void setCarbsGrams(BigDecimal carbsGrams) { this.carbsGrams = carbsGrams; }
    public BigDecimal getFatGrams() { return fatGrams; }
    public void setFatGrams(BigDecimal fatGrams) { this.fatGrams = fatGrams; }
    public BigDecimal getFiberGrams() { return fiberGrams; }
    public void setFiberGrams(BigDecimal fiberGrams) { this.fiberGrams = fiberGrams; }
    public Integer getSodiumMg() { return sodiumMg; }
    public void setSodiumMg(Integer sodiumMg) { this.sodiumMg = sodiumMg; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
