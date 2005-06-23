<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <chapter id="taglib">
      <title>Tobago Tag Library</title>
      <xsl:apply-templates select="//tag"/>
    </chapter>
  </xsl:template>

  <xsl:template match="tag">
    <xsl:param name="tagname" select="name"/>
    <sect1 id="tag-{$tagname}">
      <title>Tag
        <classname>
          <xsl:value-of select="name"/>
        </classname>
        <indexterm>
          <primary>tag</primary>
          <secondary>
            <xsl:value-of select="name"/>
          </secondary>
        </indexterm>
      </title>
      <para>
        <xsl:value-of select="description"/>
      </para>
      <variablelist>
        <varlistentry>
          <term>Class</term>
          <listitem>
            <para>
              <xsl:value-of select="tag-class"/>
            </para>
          </listitem>
        </varlistentry>
        <varlistentry>
          <term>BodyContent</term>
          <listitem>
            <para>
              <xsl:value-of select="body-content"/>
            </para>
          </listitem>
          <listitem>
            <para>
              <xsl:value-of select="body-content-description"/>
            </para>
          </listitem>
        </varlistentry>
        <varlistentry>
          <term>Description</term>
          <listitem>
            <para>
              <xsl:value-of select="description"/>
            </para>
          </listitem>
        </varlistentry>
      </variablelist>
      <table>
        <title>Attributes of tag
          <classname>
            <xsl:value-of select="name"/>
          </classname>
        </title>
        <tgroup cols="6">
          <colspec colname="name" colwidth="2.5cm" align="left"/>
          <colspec colname="required" colwidth="0.8cm" align="center"/>
          <colspec colname="expr" colwidth="0.8cm" align="center"/>
          <colspec colname="type" colwidth="2.5cm" align="left" />
          <colspec colname="default" colwidth="1.2cm" align="left"/>
          <colspec colname="description" colwidth="8.2cm" align="left"/>
          <thead>
            <row>
              <entry>Name</entry>
              <entry>Req</entry>
              <entry>Exp</entry>
              <entry>Type</entry>
              <entry>Def</entry>
              <entry>Description</entry>
            </row>
          </thead>
          <tbody>
            <xsl:apply-templates select="attribute">
              <xsl:sort select="name"/>
            </xsl:apply-templates>
          </tbody>
        </tgroup>
      </table>
      <!--      <footnoteref></footnoteref>-->
    </sect1>
  </xsl:template>

  <xsl:template match="attribute">
    <xsl:param name="required" select="required"/>
    <xsl:param name="expression" select="ui-attribute-expression"/>
    <xsl:param name="type-package" select="ui-attribute-type/class/package"/>
    <xsl:param name="type-class" select="ui-attribute-type/class/name"/>

    <row>
      <entry>
        <xsl:value-of select="name"/>
        <indexterm>
          <primary>attribute</primary>
          <secondary>
            <xsl:value-of select="name"/>
          </secondary>
        </indexterm>
      </entry>
      <entry>
        <xsl:if test="$required = 'true'">X</xsl:if>
      </entry>
      <entry>
        <xsl:if test="$expression != 'NONE'">
          <xsl:value-of select="ui-attribute-expression"/>
        </xsl:if>
      </entry>
      <entry>
        <simplelist>
          <xsl:apply-templates select="ui-attribute-type/class">
            <xsl:sort select="name"/>
          </xsl:apply-templates>
        </simplelist>
      </entry>
      <entry>
        <xsl:value-of select="ui-attribute-default-value"/>
      </entry>
      <entry>
        <xsl:value-of select="description"/>
      </entry>
    </row>
  </xsl:template>

  <xsl:template match="ui-attribute-type/class">
    <xsl:param name="type-package" select="package"/>
    <xsl:param name="type-class" select="name"/>
    <xsl:param name="attribute-name" select="../../name"/>
    <member>
      <xsl:value-of select="name"/>
      <xsl:if test="not(starts-with($type-package,'java'))">
        <footnote id="{$attribute-name}-{$type-package}.{$type-class}">
          <para><xsl:value-of select="package"/></para>
        </footnote>
      </xsl:if>
    </member>
  </xsl:template>

</xsl:stylesheet>
