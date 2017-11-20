组件使用说明
====

描述框架中自定义的组件及使用说明。

### 1. 页面组件

#### 1.1. Freemarker扩展组件

* bean

		说明：
			用于获取Spring中注册的bean，可以在freemarker中直接访问bean中的方法。
		实现类：
			org.iff.infra.util.freemarker.model.BeanMethod
		参数：
			beanName：对应Spring中注册的Bean的名称。
		使用说明：
			bean(beanName)
		使用示例：
			<#assign account = bean("authAccountApplication").getAuthAccountById(id) />
			获得Spring的Bean：authAccountApplication，并访问getAuthAccountById()方法。
		使用场景：
			可以在列表页面、修改页面、信息页面上直接获取数据，用于页面显示。
		使用限制：
			只能用于数据查找显示，不要访问数据修改方法接口，不能使用来修改数据。

* helper

		说明：
			用于访问静态方法，对于org.iff.infra.util包下的工具，不需要带包名。
		实现类：
			org.iff.infra.util.freemarker.model.HelperMethod
		参数：
			className：Java类的全称。
		使用说明：
			helper(className)
		使用示例：
			<#assign time = helper("java.lang.System").currentTimeMillis() />
			获得java.lang.System类的描述，并访问静态currentTimeMillis()方法。
			<#assign uuid = helper("StringHelper").uuid() />
			访问工具类中的StringHelper，并访问静态uuid()方法。
		使用场景：
			使用一些工具类。
		使用限制：
			无。

* i18n

		说明：
			国际化。
		实现类：
			org.iff.infra.util.freemarker.model.I18nMethod
		参数：
			key：对应properties文件中的名称。
			alias：默认输出，如果没有在properties中文件找到对应的key，则使用默认输出，建议都填写alias，方便阅读。
			namespace：命名空间，会默认把namespace添加到key前面，形成{namespace}.{key}名称。
		使用说明：
			i18n(key, alias, namespace) 
		使用示例：
			${i18n('SysDictionary.id', '主键')}
			根据当前request设置的locale，输出结果，如果没有该国际化配置则输出“主键”。
		使用场景：
			国际化。
		使用限制：
			无。

* hasRole

		说明：
			判断当前用户是否分配了指定的角色，会检测是否满足所有角色，如果当前用户是“ADMIN”角色，则返回TRUE。
		实现类：
			org.iff.infra.util.freemarker.model.HasRoleMethod
		参数：
			roleCode：角色代码，大写。
		使用说明：
			hasRole(roleCode1, roleCode2, roleCode3) 
		使用示例：
			<#if hasRole("USER", "TEST") > success output. </#if>
			查看当前用户是否有"USER", "TEST"角色。
		使用场景：
			权限控制。
		使用限制：
			无。

* hasPermit

		说明：
			判断当前用户是否分配了指定的资源，会检测是否满足所有资源，如果当前用户是“ADMIN”角色，则返回TRUE。
		实现类：
			org.iff.infra.util.freemarker.model.HasPermitMethod
		参数：
			resourceCode：资源代码。
		使用说明：
			hasPermit(resourceCode1, resourceCode2, resourceCode3) 
		使用示例：
			<#if hasPermit("Button1", "Menu1", "URL1") > success output. </#if>
			查看当前用户是否有"Button1", "Menu1", "URL1"访问权限。
		使用场景：
			权限控制，URL访问控制。
		使用限制：
			无。

#### 1.2. Freemarker扩展标签组件

* csstm

		说明：
			引入CSS样式，可以引入CSS样式文件，也可以在标签内写CSS样式。默认路径/resource/css/，所以只需要写相对目录路径即可，
			重复加载的样式只会加载一次，该标签只在HTML代码生成前使用，因为CSS样式只在header中生效。
			也可以修改/WEB-INF/pages/_layout/_layout_index.html下的实现。
		实现类：
			org.iff.infra.util.freemarker.model.CssDirective
		参数：
			required：CSS样式文件的相对路径文件名，不是必须的。
			cssStyle：CSS样式。
		使用说明：
			<@csstm required="required">
				cssStyle
			</@csstm>
		使用示例：
			<@csstm required="admin.css">
				.info{
					background-color: red;
				}
			</@csstm>
			加载/resource/css/admin.css，并在页面中添加样式。
		使用场景：
			添加样式。
		使用限制：
			样式必须在HTML页面生成前添加，因为样式不添加在header区域是不生效的。

* jstm

		说明：
			引入JS脚本文件及编写脚本。默认路径/resource/js/，所以只需要写相对目录路径即可，重复脚本文件只会加载一次，
			该标签可以在页面任何地方编写，因为JS加载在可以页面任何地方。
			也可以修改/WEB-INF/pages/_layout/_layout_index.html下的实现。
		实现类：
			org.iff.infra.util.freemarker.model.JsDirective
		参数：
			required：JS脚本文件的相对路径文件名，不是必须的。
			jsScript：JS脚本。
		使用说明：
			<@jstm required="required">
				jsScript
			</@jstm>
		使用示例：
			<@jstm required="admin.js">
				function test(){}
			</@jstm>
			在页面末尾加载/resource/js/admin.js和JS脚本。
		使用场景：
			添加JS脚本。
		使用限制：
			无。

* mvel

		说明：
			在Freemarker模板和页面嵌入MVEL脚本语言，方便使用于需要编写代码处理的地方，如new。
		实现类：
			org.iff.infra.util.freemarker.model.MvelDirective
		参数：
			var：MVEL执行后，返回的结果的变量名称，该变量可以在Freemarker中直接使用。
			type：默认为空，如果为type=template，则把mvelScript当成是直接输出，var变量无效，其他值则表示mvelScript会返回结果。
			mvelScript：MVEL脚本，可以使用Java语法。
		使用说明：
			<@mvel var="var" type="type">
				mvelScript
			</@mvel>
		使用示例：
			<@mvel var="page">return org.iff.infra.util.mybatis.plugin.Page.offsetPage(0,10000,null);</@mvel>
			直接访问表态方法，并返回值
			<@mvel var="vo">return new com.foreveross.system.infra.vo.auth.AuthAccountVO();</@mvel>
			直接创建对象并返回值
		使用场景：
			创建对象，或访问静态方法。
		使用限制：
			无。

#### 1.3. Freemarker页面标签组件

* <@fieldDesc/>

		说明：
			这个是定义CRUD页面字段的标签，在自动生成的CRUD页面中，这个标签非常重要。
		参数：
			name       ：字段显示的名称，可中文或国际化，如：<@fieldDesc name=i18n('AuthAccount.description', '描述') field="description"/>。
			field      ：字段的属性名称，对应Java中属性名称，如：<@fieldDesc name=i18n('AuthAccount.description', '描述') field="description"/>。
			type="text"：字段的类型，允许值有：'text', 'password','checkbox','radio','textarea','select','date','date'，基于上是对应于HTML表单的类型。
			class=""   ：对应HTML元素的class属性。
			props=""   ：元素的其他属性可以添加到props中，如：<@fieldDesc name='描述' field="description" type="textarea" props="cols='40' rows='2'"/>。
			width=""   ：对应HTML元素的with属性。
			height=""  ：对应HTML元素的height属性。
			dataMap={} ：当type="select"时，dataMap可以是一个下拉列表数据，如：<@fieldDesc name='状态' field="status" type="select" dataMap={"Y":"正常","N":"锁定"}/>。
			eval=false ：当标签内容有HTML，并希望在生成表单时再填充值，可以设置为true，并且HTML内容必须要在<#noparse></#noparse>标签内，（因为Freemarker在加载页面时会就会填充页面的变量），
			             内置的变量有：对于填充表单的数据有变量“data”，可以使用${data.id}获得表单数据；对于当前字段属性有值变量“fieldValue”，可以使用${fieldValue}获得当前字段值。
			listable="plain"  ：在列表页面显示的方式，允许值有：default, content, plain, none。
			editable="default"：在编辑页面显示的方式，允许值有：default, content, plain, none。
			addable="default" ：在添加页面显示的方式，允许值有：default, content, plain, none。
			showable="plain"  ：在信息页面显示的方式，允许值有：default, content, plain, none。
			searchable="none" ：在搜索页面显示的方式，允许值有：default, content, plain, none，只有default和content生效。
			defaultValue=""   ：默认值，当type="select"，可以指定其默认值。
			--- 下面是listable，editable，addable，showable，searchable的值说明 ---
			default           ：根据type的类型，默认生成表单的HTML，如：<input id=field name=field type='text' value=''/>。
			content           ：只显示标签内容，如：<@fieldDesc ...> <b>BOLD</b> </@fieldDesc>，输出：<b>BOLD</b>。
			plain             ：只显示值，如：<@fieldDesc name='状态' field="status" type="select" dataMap={"Y":"正常","N":"锁定"}/>，输出：正常。
			none              ：不显示。
		使用说明：
			<@fieldDesc name=name field="id"/>
		使用示例：
			<@fieldDesc name=i18n('AuthAccount.id', '主键') 		field="id" 		width="20" type="hidden" addable='none' listable='default'/>
			指定“主键”字段，在表单中属于隐藏字段，在添加页面不显示，在列表页面以隐藏字段显示。
			<@fieldDesc name=i18n('AuthAccount.userId', '用户') 	field="userId" eval=true listable="none">
				<#noparse>
				<input name="userId" type="hidden" id="userId" class="required" value="${fieldValue}"/>
				<input name="userName" type="text" id="userName" readonly="readonly" pol-type="selectUser" class="required pol-dialog" pol-cb="setSelectUser" pol-width="800px"
				value="${data.userName!''}${data.userEmail?has_content?then('(${data.userEmail})','')}"/>
				</#noparse>
			</@fieldDesc>
			指定“用户”字段，该字段的输入是通过弹窗选择“pol-dialog”的方式来选择输入。
		使用场景：
			CRUD页面，搜索页面。
		使用限制：
			无。

* <@hform/>

		说明：
			自动生成添加或修改表单页面。
		参数：
			name                 ：表单的名称，对应HTML表单的name属性和id属性。
			action               ：表单的提交URL，对应HTML表的action属性。
			data={}              ：表单的数据，用于填充的表单的数据，结合fieldDesc来使用。
			method="post"        ：表单默认的提交方式，对应HTML表单的mthod属性。
			title=""             ：表单的标题，使用弹窗时会显示标题。
			class=""             ：表单的样式，对应HTML表单的class属性。
			props=""             ：表单的其他属性，对应表单元素的属性。
			grid=1               ：表单的展现的列数，默认是一列，字段比较多的情况可以设置为2或更大。
			nestedOnly=false     ：只显示标签内容。
			showBtn="submit,back"：显示表单的按钮，默认显示提交和返回。
		使用说明：
			<@hform name=name action=action/>
		使用示例：
			<@hform name="firstform" action=ctx+"/auth/authaccount/addAuthAccount.do" title="添加账号"/>
			生成一个新增表单。
		使用场景：
			CRUD页面，新增、修改、搜索表单页面。
		使用限制：
			无。

* <@page_list/>

		说明：
			自动生成列表页面。
		参数：
			page           ：分页数据，org.iff.infra.util.mybatis.plugin.PagePlugin。
			url=helper('ActionHelper').fixQueryString("${ctx}${request.requestUri}?${requestQueryString!''}","pageNo")：默认为当前页面URL。
			dbClick=""     ：双击记录事件，如果指定了这个事件，那下面dbOpen不会生效。
			dbOpen="info"  ：双击打开页面，该值一般有：search, add, edit, info，或其他的pol-type。
			dbSelect=""    ：双击并选择记录，并返回指定结果，一般用于弹窗选择。
			select="1"     ：单击，其他值时为双击。
			tableOnly=false：只显示table数据，不显示分页。
			checkbox=true  ：是否显示checkbox。
		使用说明：
			<@page_list page=page/>
		使用示例：
			<@page_list page=page url=helper('ActionHelper').fixQueryString("${requestUrl}","pageNo") />
			生成分页页面。
		使用场景：
			CRUD页面，新增、修改、搜索表单页面。
		使用限制：
			无。

* <@showInfo/>

		说明：
			自动生成信息页面。
		参数：
			data       ：信息页面数据。
			title=""   ：弹窗标题。
			grid=1     ：信息页面的展现的列数，默认是一列，字段比较多的情况可以设置为2或更大。
		使用说明：
			<@showInfo data=data/>
		使用示例：
			<@showInfo data=vo title="账号信息显示" />
			自动生成信息页面。
		使用场景：
			CRUD页面，新增、修改、搜索表单页面。
		使用限制：
			无。

* <@hsearch/>

		说明：
			自动生成信息页面。
		参数：
			name         ：搜索表单名称，对应HTML表单的name与id属性。
			data={}      ：搜索表单默认值。
			title=""     ：标题。
			class=""     ：对应HTML的class属性。
			props=""     ：对应HTML的属性。
			method="post"：对应HTML表单的method属性。
			show=false   ：默认不显示搜索表单。
		使用说明：
			<@hsearch name=name/>
		使用示例：
			<@hsearch name="searchForm" />
			自动生成搜索页面。
		使用场景：
			CRUD页面，新增、修改、搜索表单页面。
		使用限制：
			无。

* <@simpleTree/>

		说明：
			树型组件。
		参数：
			el                ：树型组件绑定的元素，使用jquery表达式，如，#tree。
			url=""            ：动态获取数据JSON数据，与下面data二选一。
			data=[]           ：树数据。
			selected=[]       ：选中的数据。
			selectedId="id"   ：选择的数据对应的字段。
			id="id"           ：树的ID对应数据的字段。
			pid="pid"         ：树的父结点ID。
			rootPid=""        ：树的根节点ID。
			resultFields="id" ：返回的结果集，可以指定多个字段。
			callback=""       ：返回结果集的回调函数，函数写在回调的页面。
			checkbox=true     ：是否显示选择框。
			processTreeData=""：显示树前预处理数据。
			expandAll=true    ：是否展开所有节点。
		使用说明：
			<@simpleTree el=el/>
		使用示例：
			<@simpleTree el="#ztree" data=orgs id="id" pid="parentId" resultFields="id,parentId,type1,description,name,type2,code" callback="setSelectNode" checkbox=false />
			自动生成树型结构。
		使用场景：
			CRUD页面，新增、修改、搜索表单页面。
		使用限制：
			无。


* <#macro paginate pageSize=10 currentPage=0 totalPage=0 totalCount=0 url=''>

* <#macro fvalidate formName type="add">

* <#macro showField values data field able>

* <#macro hselect name class="" props="" dataMap={} selected="" defaultValue="">


----------