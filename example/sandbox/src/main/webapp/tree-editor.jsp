<%--
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
--%>

<%@ taglib uri="http://myfaces.apache.org/tobago/sandbox" prefix="tcs" %>
<%@ taglib uri="http://myfaces.apache.org/tobago/component" prefix="tc" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:view>
  <tc:loadBundle basename="demo" var="bundle"/>

  <tc:page label="Sandbox - Tree Editor" id="page"
           width="500px" height="800px">
    <f:facet name="layout">
      <tc:gridLayout margin="10px" rows="fixed;300px;*"/>
    </f:facet>

    <tc:toolBar>
      <tc:toolBarCommand action="#{controller.createNode}" label="New"/>
      <tc:toolBarCommand action="#{controller.deleteNode}" label="Delete"/>
    </tc:toolBar>

    <tcs:tree state="#{controller.state}" id="tree">
      <!--
      fixme: The helping-node "Root" is required in the moment, has to be removed!
      -->
      <tcs:treeNode label="Root">
        <tcs:treeData value="#{controller.tree}" var="node" id="data">
          <tcs:treeNode label="#{node.userObject.name}" id="template"
                        markup="#{node.userObject.markup}"
                        tip="#{node.userObject.tip}"
                        action="#{node.userObject.action}" value="#{node}"/>
        </tcs:treeData>
      </tcs:treeNode>
    </tcs:tree>

    <tc:cell/>

  </tc:page>
</f:view>
