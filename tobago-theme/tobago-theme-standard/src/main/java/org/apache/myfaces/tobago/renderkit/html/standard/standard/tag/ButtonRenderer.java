/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.myfaces.tobago.renderkit.html.standard.standard.tag;

import org.apache.myfaces.tobago.component.Attributes;
import org.apache.myfaces.tobago.component.UIButton;
import org.apache.myfaces.tobago.internal.component.AbstractUICommand;
import org.apache.myfaces.tobago.internal.component.AbstractUIForm;
import org.apache.myfaces.tobago.internal.component.AbstractUIToolBar;
import org.apache.myfaces.tobago.internal.util.AccessKeyLogger;
import org.apache.myfaces.tobago.renderkit.CommandRendererBase;
import org.apache.myfaces.tobago.renderkit.LabelWithAccessKey;
import org.apache.myfaces.tobago.renderkit.css.BootstrapClass;
import org.apache.myfaces.tobago.renderkit.css.Classes;
import org.apache.myfaces.tobago.renderkit.html.Command;
import org.apache.myfaces.tobago.renderkit.html.CommandMap;
import org.apache.myfaces.tobago.renderkit.html.DataAttributes;
import org.apache.myfaces.tobago.renderkit.html.HtmlAttributes;
import org.apache.myfaces.tobago.renderkit.html.HtmlButtonTypes;
import org.apache.myfaces.tobago.renderkit.html.HtmlElements;
import org.apache.myfaces.tobago.renderkit.html.JsonUtils;
import org.apache.myfaces.tobago.renderkit.html.util.HtmlRendererUtils;
import org.apache.myfaces.tobago.util.ComponentUtils;
import org.apache.myfaces.tobago.webapp.TobagoResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;

public class ButtonRenderer extends CommandRendererBase {

  private static final Logger LOG = LoggerFactory.getLogger(ButtonRenderer.class);

  @Override
  public void encodeEnd(final FacesContext facesContext, final UIComponent component) throws IOException {

    final AbstractUICommand button = (AbstractUICommand) component;
    final String clientId = button.getClientId(facesContext);
    final boolean disabled = button.isDisabled();
    final LabelWithAccessKey label = new LabelWithAccessKey(button);

    final TobagoResponseWriter writer = HtmlRendererUtils.getTobagoResponseWriter(facesContext);

    writer.startElement(HtmlElements.BUTTON, button);
    writer.writeAttribute(HtmlAttributes.TYPE, HtmlButtonTypes.BUTTON, false);
    writer.writeNameAttribute(clientId);
    writer.writeIdAttribute(clientId);
    HtmlRendererUtils.writeDataAttributes(facesContext, writer, button);
    final String title = HtmlRendererUtils.getTitleFromTipAndMessages(facesContext, button);
    if (title != null) {
      writer.writeAttribute(HtmlAttributes.TITLE, title, true);
    }
    writer.writeAttribute(HtmlAttributes.DISABLED, disabled);

    if (!disabled) {
      final CommandMap map = new CommandMap(new Command(facesContext, button));
      writer.writeAttribute(DataAttributes.COMMANDS.getValue(), JsonUtils.encode(map), true);

      writer.writeAttribute(HtmlAttributes.HREF, "#", false);

      if (label.getAccessKey() != null) {
        writer.writeAttribute(HtmlAttributes.ACCESSKEY, Character.toString(label.getAccessKey()), false);
        AccessKeyLogger.addAccessKey(facesContext, label.getAccessKey(), clientId);
      }

      if (button instanceof UIButton) {
        final Integer tabIndex = ((UIButton) button).getTabIndex();
        if (tabIndex != null) {
          writer.writeAttribute(HtmlAttributes.TABINDEX, tabIndex);
        }
      }
    }

    writer.writeStyleAttribute(button.getStyle());

    final boolean defaultCommand = ComponentUtils.getBooleanAttribute(component, Attributes.DEFAULT_COMMAND);
    // TODO this might be too expensive:
    // TODO please put a flag in the ToolBar-handler and Button-handler (facelets-handler)
    final boolean insideToolbar = ComponentUtils.findAncestor(component, AbstractUIToolBar.class) != null;
    writer.writeClassAttribute(
        Classes.create(button),
        BootstrapClass.BTN,
        defaultCommand ? BootstrapClass.BTN_PRIMARY : BootstrapClass.BTN_DEFAULT,
        insideToolbar ? BootstrapClass.NAVBAR_BTN : null,
        button.getCustomClass());

    if (button instanceof UIButton && ((UIButton) component).isDefaultCommand()) {
      final AbstractUIForm form = ComponentUtils.findAncestor(component, AbstractUIForm.class);
      writer.writeAttribute(DataAttributes.DEFAULT.getValue(), form.getClientId(facesContext), false);
    }
    writer.flush(); // force closing the start tag

    String image = (String) button.getAttributes().get(Attributes.IMAGE);
    HtmlRendererUtils.encodeIconWithLabel(writer, facesContext, image, label, disabled);

    writer.endElement(HtmlElements.BUTTON);
  }
}
