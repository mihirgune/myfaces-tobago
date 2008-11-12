package org.apache.myfaces.tobago.renderkit.html.scarborough.standard.tag;

/*
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
 */

/*
 * Created 07.02.2003 16:00:00.
 * $Id$
 */

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.tobago.component.AbstractUITree;
import org.apache.myfaces.tobago.component.AbstractUITreeNode;
import org.apache.myfaces.tobago.component.Attributes;
import org.apache.myfaces.tobago.component.Facets;
import org.apache.myfaces.tobago.component.UITree;
import org.apache.myfaces.tobago.component.UITreeNode;
import org.apache.myfaces.tobago.context.ResourceManagerUtil;
import org.apache.myfaces.tobago.context.ResourceUtils;
import org.apache.myfaces.tobago.model.MixedTreeModel;
import org.apache.myfaces.tobago.renderkit.CommandRendererBase;
import org.apache.myfaces.tobago.renderkit.html.HtmlAttributes;
import org.apache.myfaces.tobago.renderkit.html.HtmlConstants;
import static org.apache.myfaces.tobago.renderkit.html.HtmlConstants.A;
import static org.apache.myfaces.tobago.renderkit.html.HtmlConstants.DIV;
import static org.apache.myfaces.tobago.renderkit.html.HtmlConstants.IMG;
import static org.apache.myfaces.tobago.renderkit.html.HtmlConstants.SPAN;
import org.apache.myfaces.tobago.renderkit.html.HtmlStyleMap;
import org.apache.myfaces.tobago.renderkit.html.StyleClasses;
import org.apache.myfaces.tobago.renderkit.html.util.CommandRendererHelper;
import org.apache.myfaces.tobago.renderkit.html.util.HtmlRendererUtil;
import org.apache.myfaces.tobago.renderkit.util.RenderUtil;
import org.apache.myfaces.tobago.util.ComponentUtil;
import org.apache.myfaces.tobago.webapp.TobagoResponseWriter;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TreeNodeRenderer extends CommandRendererBase {

  private static final Log LOG = LogFactory.getLog(TreeNodeRenderer.class);

  protected static final String OPEN_FOLDER
      = ResourceUtils.createString("image", "treeNode", "icon", "open", ResourceUtils.GIF);
  protected static final String CLOSED_FOLDER
      = ResourceUtils.createString("image", "treeNode", "icon", ResourceUtils.GIF);
  protected static final String LEAF
      = ResourceUtils.createString("image", "treeNode", "icon", "leaf", ResourceUtils.GIF);

  @Override
  public void decode(FacesContext facesContext, UIComponent component) {

    super.decode(facesContext, component);

    if (ComponentUtil.isOutputOnly(component)) {
      return;
    }

    AbstractUITreeNode node = (AbstractUITreeNode) component;
    AbstractUITree tree = node.findTree();
    String treeId = tree.getClientId(facesContext);
    String nodeStateId = node.nodeStateId(facesContext);
    Map requestParameterMap = facesContext.getExternalContext().getRequestParameterMap();
    String id = node.getClientId(facesContext);

    // expand state
    boolean expanded = Boolean.parseBoolean((String) requestParameterMap.get(id + "-expanded"));
    node.setExpanded(expanded);

    // select
    String searchString;
    if (TreeRenderer.isSelectable(tree)) { // selection
      String selected = (String) requestParameterMap.get(treeId + AbstractUITree.SELECT_STATE);
      searchString = ";" + nodeStateId + ";";
      if (StringUtils.contains(selected, searchString)) {
        // TODO: add selection to Component
        //state.addSelection((DefaultMutableTreeNode) node.getValue());
      }
    }

    // marker
    String marked = (String) requestParameterMap.get(treeId + AbstractUITree.MARKER);
    if (marked != null) {
      searchString = treeId + NamingContainer.SEPARATOR_CHAR + nodeStateId;
      node.setMarked(marked.equals(searchString));
    } else {
      LOG.warn("This log message is help clarifying the occurence of this else case.");
    }
  }

  @Override
  public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {

    UITreeNode node = (UITreeNode) component;
    UITree root = (UITree) node.findTree();
    MixedTreeModel mixedModel = root.getModel();

    mixedModel.onEncodeBegin();

    TobagoResponseWriter writer = HtmlRendererUtil.getTobagoResponseWriter(facesContext);

    String treeId = root.getClientId(facesContext);

    DefaultMutableTreeNode modelNode = (DefaultMutableTreeNode) node.getValue();

    boolean folder = mixedModel.isFolder();

    boolean marked = node.isMarked();
    String id = node.getClientId(facesContext);
    boolean expanded = node.isExpanded();
    boolean menuMode = root.getMode().equals("menu");

    boolean showIcons = root.isShowIcons();
    boolean showJunctions = root.isShowJunctions();
    boolean showRootJunction = root.isShowRootJunction();
    boolean showRoot = root.isShowRoot();
    int depth = mixedModel.getDepth();
    boolean isRoot = mixedModel.isRoot();
    boolean hasNextSibling = mixedModel.hasCurrentNodeNextSibling();
    List<Boolean> junctions = mixedModel.getJunctions();

    if (!showRoot && junctions.size() > 0) {
      junctions.remove(0);
    }

    String source;
    String openSource = null;
    String closedSource;

    String image = ComponentUtil.getStringAttribute(node, "image");
    if (image != null) { // application image
      closedSource = ResourceManagerUtil.getImageWithPath(facesContext, image);
    } else { // theme image
      closedSource = ResourceManagerUtil.getImageWithPath(facesContext, CLOSED_FOLDER);
    }
    if (folder) {
      if (image != null) { // application image
        openSource = ResourceManagerUtil.getImageWithPath(facesContext,
            ResourceUtils.addPostfixToFilename(image, "open"), true);
      } else { // theme image
        openSource = ResourceManagerUtil.getImageWithPath(facesContext, OPEN_FOLDER);
      }
      source = expanded ? openSource : closedSource;
    } else {
      if (image != null) { // application image
        source = ResourceManagerUtil.getImageWithPath(facesContext,
            ResourceUtils.addPostfixToFilename(image, "leaf"), true);
      } else { // theme image
        source = ResourceManagerUtil.getImageWithPath(facesContext, LEAF);
      }
      if (source == null) {
        source = closedSource;
      }
    }

    CommandRendererHelper helper = new CommandRendererHelper(facesContext, node);

    if (showRoot || !isRoot) {
      writer.startElement(DIV, null);

      // div id
      writer.writeIdAttribute(id);
      if (!folder) {
        HtmlRendererUtil.renderDojoDndItem(node, writer, true);
      }

      // div class (css)
      StyleClasses styleClasses = StyleClasses.ensureStyleClasses(node);
      styleClasses.updateClassAttributeAndMarkup(node, "treeNode");
      if ("menu".equals(root.getMode())) {
        styleClasses.addClass("treeNode", "menu");
        if (marked) {
          styleClasses.addClass("treeNode", "marker");
        }
      }
      styleClasses.addMarkupClass(node, "treeNode");
      writer.writeClassAttribute(styleClasses);

      // div style (width)
      Integer width = null;
      HtmlStyleMap style = (HtmlStyleMap) root.getAttributes().get(Attributes.STYLE);
      if (style != null) {
        width = style.getInt("width");
      }
      String widthString;
      if (width != null) {
        widthString = "width: " + Integer.toString(width - 22); // fixme: 4 + 18 for scrollbar
      } else {
        widthString = "100%";
      }
      writer.writeStyleAttribute(widthString);

      if (folder) {
        encodeExpandedHidden(writer, node, id, expanded);
      }

      if (folder && menuMode) {
        encodeMenuIcon(facesContext, writer, treeId, id, expanded, node);
      }

      encodeIndent(facesContext, writer, menuMode, junctions);

      encodeTreeJunction(facesContext, writer, id, treeId, showJunctions, showRootJunction, showRoot, expanded,
          folder, depth, hasNextSibling, openSource, closedSource);

      encodeTreeIcons(writer, id, treeId, showIcons, folder, source, openSource, closedSource);

      encodeLabel(writer, helper, node, marked, treeId);

      UIComponent facet = node.getFacet(Facets.ADDENDUM);
      if (facet != null) {
        RenderUtil.encode(facesContext, facet);
      }

      writer.endElement(DIV);
    }

    if (folder) {
      String contentStyle = "display: " + (expanded ? "block" : "none") + ";";
      writer.startElement(DIV, null);
      writer.writeIdAttribute(id + "-cont");
      writer.writeStyleAttribute(contentStyle);
    }
    if (LOG.isDebugEnabled()) {
      String label = node.getLabel();
      int level = modelNode.getLevel();
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < level; i++) {
        builder.append("    ");
      }
      LOG.debug(builder + "<div name=" + label + ">");
    }
  }

  private void encodeExpandedHidden(TobagoResponseWriter writer, AbstractUITreeNode node, String clientId,
                                    boolean expanded) throws IOException {
    writer.startElement(HtmlConstants.INPUT, node);
    writer.writeAttribute(HtmlAttributes.TYPE, "hidden", false);
    writer.writeNameAttribute(clientId + "-expanded");
    writer.writeIdAttribute(clientId + "-expanded");
    writer.writeAttribute(HtmlAttributes.VALUE, Boolean.toString(expanded), false);
    writer.endElement(HtmlConstants.INPUT);
  }

  private void encodeMenuIcon(
      FacesContext facesContext, TobagoResponseWriter writer, String treeId, String id, boolean expanded,
      UIComponent node)
      throws IOException {
    String menuOpen = ResourceManagerUtil.getImageWithPath(facesContext, "image/treeMenuOpen.gif");
    String menuClose = ResourceManagerUtil.getImageWithPath(facesContext, "image/treeMenuClose.gif");
    String onclick = "tobagoTreeNodeToggle(this.parentNode, '" + treeId + "', null, null, '"
        + menuOpen + "', '" + menuClose + "')";
    Object objOnclick = node.getAttributes().get("onclick");
    if (null != objOnclick) {
      onclick += ";" + objOnclick;
    }
    String src = expanded ? menuOpen : menuClose;
    writer.startElement(IMG, null);
    writer.writeClassAttribute("tobago-tree-menu-icon");
    writer.writeIdAttribute(id + "-menuIcon");
    writer.writeAttribute("src", src, true);
    writer.writeAttribute("onclick", onclick, true);
    writer.writeAttribute("alt", "", false);
    writer.endElement(IMG);
  }

  private void encodeIndent(
      FacesContext facesContext, TobagoResponseWriter writer, boolean menuMode, List<Boolean> junctions)
      throws IOException {

    String blank = ResourceManagerUtil.getImageWithPath(facesContext, "image/blank.gif");
    String perpendicular = ResourceManagerUtil.getImageWithPath(facesContext, "image/I.gif");

    for (Boolean junction : junctions) {
      writer.startElement(IMG, null);
      writer.writeClassAttribute("tree-junction");
      if (junction && !menuMode) {
        writer.writeAttribute("src", perpendicular, true);
      } else {
        writer.writeAttribute("src", blank, true);
      }
      writer.endElement(IMG);
    }
  }

  private void encodeTreeJunction(
      FacesContext facesContext, TobagoResponseWriter writer, String id, String treeId,
      boolean showJunctions, boolean showRootJunction, boolean showRoot, boolean expanded, boolean folder,
      int depth, boolean hasNextSibling, String openSource, String closedSource)
      throws IOException {
    if (!(!showJunctions
        || !showRootJunction && depth == 0
        || !showRootJunction && !showRoot && depth == 1)) {
      writer.startElement(IMG, null);
      writer.writeClassAttribute("tree-junction");
      writer.writeIdAttribute(id + "-junction");

      String gif = folder && expanded
          ? (depth == 0
          ? "Rminus.gif"
          : (hasNextSibling ? "Tminus.gif" : "Lminus.gif"))
          : ((depth == 0)
          ? "Rplus.gif"
          : (hasNextSibling)
          ? (folder ? "Tplus.gif" : "T.gif")
          : (folder ? "Lplus.gif" : "L.gif")
      );

      String src = ResourceManagerUtil.getImageWithPath(facesContext, "image/" + gif);
      writer.writeAttribute("src", src, true);
      if (folder) {
        writer.writeAttribute("onclick", createOnclickForToggle(treeId, openSource, closedSource), true);
      }
      writer.writeAttribute("alt", "", false);
//    } else if (( !this.hideRoot && depth >0 ) || (this.hideRoot && depth > 1)) {
//      str += '<img class="tree-junction" id="' + this.id
//          + '-junction" src="' + this.treeResources.getImage("blank.gif")
//          + '" alt="">';
      writer.endElement(IMG);
    }
  }

  private void encodeTreeIcons(
      TobagoResponseWriter writer, String id, String treeId,
      boolean showIcons, boolean folder, String source, String openSource, String closedSource)
      throws IOException {

    if (showIcons) {
      writer.startElement(IMG, null);
      writer.writeClassAttribute("tree-icon");
      writer.writeIdAttribute(id + "-icon");

      writer.writeAttribute("src", source, true);
      if (folder) {
        writer.writeAttribute("onclick", createOnclickForToggle(treeId, openSource, closedSource), true);
      }
      writer.writeAttribute("alt", "", false);
      writer.endElement(IMG);
    }
  }

  private String createOnclickForToggle(String treeId, String openSouce, String closedSource) {
    if (openSouce == null) { // default
      openSouce = closedSource;
    }
    return "tobagoTreeNodeToggle(this.parentNode, '" + treeId + "', '"
        + openSouce + "', '" + closedSource + "', null, null)";
  }

/*
  if (this.isFolder) {
    str += '<img class="tree-icon" id="' + this.id + '-icon" '
        + 'src="' + (this.expanded ? this.openIcon : this.icon) + ' " '
        + 'onclick="toggle(this.parentNode, \'' + this.treeHiddenId
        + '\', \'' + this.treeResources.getImage("image/treeNode-icon-open.gif")
        + '\', \'' + this.treeResources.getImage("image/treeNode-icon.gif")
        + '\')"'
        + ' alt="">';
  } else {
    str += '<img class="tree-icon" id="' + this.id
        + '-icon" src="' + this.treeResources.getImage("image/treeNode-icon-leaf.gif") + '" alt="">';
  }
*/

  private void encodeLabel(
      TobagoResponseWriter writer, CommandRendererHelper helper, UITreeNode node, boolean marked, String treeId)
      throws IOException {

    if (helper.isDisabled()) {
      writer.startElement(SPAN, null);
    } else {
      writer.startElement(A, null);
      writer.writeAttribute(HtmlAttributes.HREF, helper.getHref(), true);
      writer.writeAttribute(HtmlAttributes.ONCLICK, helper.getOnclick(), true); // xxx is escaping required?
      writer.writeAttribute(
          HtmlAttributes.ONFOCUS, "Tobago.Tree.storeMarker(this.parentNode, '" + treeId + "')", false);
    }
    if (marked) {
      StyleClasses classes = new StyleClasses();
      classes.addClass("treeNode", "marker");
      writer.writeClassAttribute(classes);
    }
    String objTip = node.getTip();
    if (objTip != null) {
//XXX is needed?      tip = StringEscapeUtils.escapeJavaScript(tip);
      writer.writeAttribute(HtmlAttributes.TITLE, String.valueOf(objTip), true);
    }
    String label = node.getLabel();
    if (label == null) {
      LOG.warn("label == null");
      label = "label";
    }
    writer.writeText(label);
    if (helper.isDisabled()) {
      writer.endElement(SPAN);
    } else {
      writer.endElement(A);
    }
  }

  @Override
  public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {

    UITreeNode node = (UITreeNode) component;
    UITree root = (UITree) node.findTree();
    MixedTreeModel mixedModel = root.getModel();
    boolean folder = mixedModel.isFolder();

    mixedModel.onEncodeEnd();

    String id = node.getClientId(facesContext);

    TobagoResponseWriter writer = HtmlRendererUtil.getTobagoResponseWriter(facesContext);

    if (folder) {
      writer.endElement(DIV);
      writer.writeComment("\nend of " + id + "-cont ");
    }

    if (LOG.isDebugEnabled()) {
      String label = node.getLabel();
      int level = mixedModel.getDepth();
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < level; i++) {
        builder.append("    ");

      }
      LOG.debug(builder + "</div> <!-- " + label + " -->");
    }
  }

}
