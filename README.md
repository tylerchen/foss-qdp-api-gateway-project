# foss-qdp-project

## 权限模块

### 帐户与用户（一对一）

登录的帐户与用户，用户必须要有相关的登录帐号才能登录系统

Account       <-1----1->       User

### 帐户与角色（一对多）

登录的帐号与角色关联，一个登录帐号可以有多个角色，如系统管理员等

Account       <-1--TYPE1=SYS_DEF,TYPE2=ACCOUNT     --*->       Role

### 组织与角色（一对多）

组织机构与角色关联，如部门经理，项目经理

Organization  <-1--TYPE1=SYS_DEF,TYPE2=ORGANIZATION--*->       Role

### 组织与菜单（一对多）

一个组织可以挂一个菜单，如公司机构可以挂管理公司的菜单，如项目组织可以挂项目管理菜单

Organization  <-*----*->       Menu

### 用户与组织（一对多）

一个用户可以在多个组织下工作，如公司组织机构，项目组织等

User          <-*----*->       Organization

### 资源与角色（多对一）

表示一个角色可以访问哪些资源

Resource      <-*----1->       Role

### 菜单与资源（一对一）

菜单是一种特殊类型的资源

Menu          <-1----1->       Resource

### 查找一个帐号的所有角色

1. 查找Account-Role

2. 查找Account-User-Organization-Role


## Mybatis 命名查询

### 命名规则

1. 查询单条结果

get+对象名+[Map]+By+条件字段名， 如通过菜单id查询菜单【getMenuById】，如果返回集合为Map【getMenuMapById】，多条件如【getMenuByIdAndName】

2. 查询多条结果

find+对象名+[Map]+By+条件字段名， 如通过菜单name查询菜单【findMenuByName】，如果返回集合为Map【findMenuMapByName】，多条件如【findMenuByNameAndParent】

3. 查询记录数

count+对象名+By+条件字段名， 如通过菜单name查询菜单总数【countMenuByName】









