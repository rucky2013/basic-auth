package com.github.ququzone.basicauth.persistence;

import com.github.ququzone.basicauth.model.Menu;
import com.github.ququzone.basicauth.model.MenuResource;
import com.github.ququzone.common.MybatisMapper;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * menu mapper.
 *
 * @author Yang XuePing
 */
@MybatisMapper
public interface MenuMapper {
    @Select("select id, name, icon, order_num, created_time, updated_time from menus where id = #{id}")
    @ResultMap("MenuResult")
    Menu find(String id);

    @Select("select id, name, icon, order_num, created_time, updated_time from menus order by order_num")
    @ResultMap("MenuResult")
    List<Menu> all();

    @Select("select count(1) from menus")
    Long count();

    @Insert("insert into menus (id, name, icon, order_num, created_time)" +
            " values (#{id}, #{name}, #{icon}, #{orderNum}, #{createdTime})")
    void insert(Menu menu);

    @Update("update menus set name = #{name}, icon = #{icon}, updated_time = #{updatedTime} where id = #{id}")
    void update(@Param("id") String id, @Param("name") String name,
                @Param("icon") String icon, @Param("updatedTime") Date updatedTime);

    @Delete("delete from menu_resources where menu_id = #{menuId}")
    void deleteMenuResources(@Param("menuId") String menuId);

    @Delete("delete from menus where id = #{id}")
    void delete(@Param("id") String id);

    @Insert("insert into menu_resources(id, menu_id, resource_id, order_num, created_time)" +
            " values (#{id}, #{menuId}, #{resourceId}, #{orderNum}, #{createdTime})")
    void insertMenuResource(MenuResource menuResource);

    @Select("select count(1) from menu_resources where menu_id = #{menuId}")
    Long countResource(@Param("menuId") String menuId);

    @Delete("delete from menu_resources where menu_id = #{menuId} and resource_id = #{resourceId}")
    void deleteMenuResource(@Param("menuId") String menuId, @Param("resourceId") String resourceId);

    @Update("update menus set order_num = #{orderNum} where id = #{id}")
    void updateOrderNum(@Param("id") String id, @Param("orderNum") Integer orderNum);

    @Select("select max(order_num) from menus")
    Integer selectMenuMaxOrder();

    @Select("select max(order_num) from menu_resources where menu_id = #{menuId}")
    Integer selectMenuResourceMaxOrder(@Param("menuId") String menuId);
}
