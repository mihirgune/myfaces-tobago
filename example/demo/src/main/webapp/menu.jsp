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
<%@ taglib uri="http://myfaces.apache.org/tobago/component" prefix="tc" %>
<%@ taglib uri="http://myfaces.apache.org/tobago/extension" prefix="tx" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%-- !!!!!! no <f:subview here !!!!!!! --%>
      <f:facet name="menuBar" >
        <tc:menuBar >
          <tc:menu label="#{overviewBundle.menu_config}" >
            <tc:menu label="#{overviewBundle.menu_themes}" >
              <tc:menuItem action="#{clientConfigController.submit}">
                <f:facet name="items">
                  <tc:selectOneRadio value="#{clientConfigController.theme}" >
                    <f:selectItems value="#{clientConfigController.themeItems}" />
                  </tc:selectOneRadio>
                </f:facet>
              </tc:menuItem>
            </tc:menu>
            <tc:menu label="#{overviewBundle.menu_locale}">
              <tx:menuRadio action="#{clientConfigController.submit}"
                            value="#{clientConfigController.locale}">
                <f:selectItems value="#{clientConfigController.localeItems}" />
              </tx:menuRadio>
            </tc:menu>
            <%-- todo: may have something like immediate="true", but in this case, the value will not switched --%>
            <tc:menuCheckbox action="#{clientConfigController.submit}"
                label="#{overviewBundle.menu_debug}"
                value="#{clientConfigController.debugMode}"/>
            <tc:menuItem action="#{demo.resetSession}" label="Reset" />
          </tc:menu>

          <tc:menu label="#{overviewBundle.menu_help}">
            <tc:menuItem onclick="alert('#{overviewBundle.pageTitle}' + String.fromCharCode(10) + '#{info.version}' + String.fromCharCode(10) + '#{overviewBundle.tobago_url}' + String.fromCharCode(10))"
                         label="#{overviewBundle.menu_about}" />
            <tc:menuItem onclick="LOG.show();"
                         label="#{overviewBundle.menu_showLog}"
                         rendered="#{clientConfigController.debugMode}"/>
            <tc:menuItem action="server-info" immediate="true"
                         label="Server Info"
                         disabled="#{! info.enabled}"/>
          </tc:menu>
        </tc:menuBar>

      </f:facet>
