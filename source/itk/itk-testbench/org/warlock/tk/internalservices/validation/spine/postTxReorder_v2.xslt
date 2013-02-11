<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mif="urn:hl7-org:v3/mif"
	xmlns:h="urn:hl7-org:v3" exclude-result-prefixes="h npfitlc"  xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:npfitlc="NPFIT:HL7:Localisation">
	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes" />

	<xsl:template match="h:ClinicalDocument" mode="re-order">
		<xsl:comment>
			This example message is provided for illustrative purposes only. It
			has had no clinical validation. Whilst every effort has been taken to
			ensure that the examples are consistent with the
			message specification
			contained within the MiM, where there are conflicts
			with the written
			message specification or schema, the specification
			or schema shall be
			considered to take
			precedence
		</xsl:comment>
		<xsl:copy>
			<!--
				<ClinicalDocument xmlns="urn:hl7-org:v3"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xmlns:npfitlc="NPFIT:HL7:Localisation">
			-->

			<xsl:copy-of select="@*" />
			<xsl:call-template name="sortChildren">
				<xsl:with-param name="assocName" select="'ClinicalDocument'"></xsl:with-param>
			</xsl:call-template>
		</xsl:copy>
	</xsl:template>

	<xsl:template name="sortChildren">
		<xsl:param name="assocName" select="local-name(current())" />
		<xsl:variable name="rimClass" as="xs:string+">

			<xsl:choose>
				<xsl:when test="$assocName='component'">ActRelationship</xsl:when>
				<xsl:when test="$assocName='ClinicalDocument'">Document</xsl:when>
				<xsl:when test="$assocName='participant'">
					<xsl:choose>
					<xsl:when test="local-name(..)='informant'">Role</xsl:when>
					<xsl:otherwise>Participation</xsl:otherwise>				
					</xsl:choose>
				</xsl:when>
				
				<xsl:when test="$assocName='informationRecipient'">
					<xsl:choose>
						<xsl:when test="@determinerCode">Person</xsl:when>
						<xsl:otherwise>Participation</xsl:otherwise>
					</xsl:choose>
				</xsl:when>


				<xsl:when test="$assocName='location'">
				<xsl:choose>
				<xsl:when test="contains(local-name(..),'ncompassingEncounter')">Participation</xsl:when>
					<xsl:when test="local-name(..)='healthCareFacility'">Place</xsl:when>
					<xsl:otherwise>Participation</xsl:otherwise>
				</xsl:choose>
				</xsl:when>

				<xsl:when
					test="document('SerialisedCDAModel.xml')//mif:targetConnection[@name=$assocName]">
					<xsl:choose>
						<xsl:when
							test="document('SerialisedCDAModel.xml')//mif:targetConnection[@name=$assocName]/mif:participantClass/mif:class">
							<xsl:sequence
								select="document('SerialisedCDAModel.xml')//mif:association/mif:targetConnection[@name=$assocName]/mif:participantClass/mif:class/mif:derivedFrom/@className" />
						</xsl:when>
						<xsl:when
							test="document('SerialisedCDAModel.xml')//mif:targetConnection[@name=$assocName]/mif:participantClass/mif:reference">
							<xsl:variable name="refClass"
								select="document('SerialisedCDAModel.xml')//mif:targetConnection[@name=$assocName]/mif:participantClass/mif:reference/@name" />
							<xsl:sequence
								select="document('SerialisedCDAModel.xml')//mif:class[@name=$refClass]/mif:derivedFrom/@className" />
						</xsl:when>
						<xsl:otherwise>unknown</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				
				<xsl:when
					test="document('SerialisedCDAModel.xml')//mif:targetConnection/mif:participantClassSpecialization[@traversalName=$assocName]">
<!--					Choice box-->
					<xsl:variable name="choiceClass"
						select="document('SerialisedCDAModel.xml')//mif:targetConnection/mif:participantClassSpecialization[@traversalName=$assocName]/@className" />
					<xsl:sequence
						select="document('SerialisedCDAModel.xml')//mif:targetConnection/mif:participantClassSpecialization[@traversalName=$assocName]/following-sibling::mif:participantClass/mif:class/mif:childClass[@className=$choiceClass]/mif:specializedClass/mif:class/mif:derivedFrom/@className">
					</xsl:sequence>
				</xsl:when>
				<xsl:otherwise>
					<xsl:sequence
						select="document('SerialisedCDAModel.xml')//mif:targetConnection[@name=$assocName]/mif:participantClass/mif:class/mif:derivedFrom/@className" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

<!--	<xsl:comment>RIM class is <xsl:value-of select="$rimClass">
		</xsl:value-of> 
		</xsl:comment>	-->
		
		<xsl:for-each select="child::*[not(@typeCode)][not(@classCode)][not(local-name()='languageCommunication')]">
			<xsl:sort
				select="count(document('sortedMifClassAtts.xml')//sortClass[@name=$rimClass[1]]/Attribute[@name=local-name(current())]/preceding-sibling::*)"
				data-type="number" />
			<xsl:copy-of select="." />
		</xsl:for-each>

		<xsl:for-each select="child::*[@typeCode | @classCode] | h:languageCommunication">
			<xsl:sort data-type="number" order="ascending">
				<!--
					<xsl:sort
					select="document('SerialisedCDAModel.xml')//mif:entryClass/mif:class/mif:association[mif:targetConnection/@name=local-name(current())]/@sortKey"
					data-type="number">
				-->
				
				<xsl:choose>
					<xsl:when test="$assocName='ClinicalDocument'">
						<xsl:value-of
							select="document('SerialisedCDAModel.xml')//mif:entryClass/mif:class/mif:association[mif:targetConnection/@name=local-name(current())]/@sortKey" />
					</xsl:when>

					<xsl:when
						test="document('SerialisedCDAModel.xml')//mif:targetConnection[@name=$assocName]">
						<xsl:value-of
							select="document('SerialisedCDAModel.xml')//mif:association[1]/mif:targetConnection[@name=$assocName]/mif:participantClass/mif:class/mif:association[mif:targetConnection/@name=local-name(current())]/@sortKey" />
					</xsl:when>
					
					<xsl:when
						test="document('SerialisedCDAModel.xml')//mif:targetConnection/mif:participantClassSpecialization[@traversalName=$assocName]">
						<!-- for choice classes need to construct a sequence containing value of  sortKey of alll the decendents i.e. children of the top level choice class and children of each specializedClass and  -->
						<xsl:choose>
							<xsl:when test="document('SerialisedCDAModel.xml')//mif:targetConnection/mif:participantClassSpecialization[@traversalName=$assocName][1]/following-sibling::mif:participantClass/mif:class/mif:association[mif:targetConnection/@name=local-name(current())]/@sortKey">
								<xsl:sequence
							select="document('SerialisedCDAModel.xml')//mif:targetConnection/mif:participantClassSpecialization[@traversalName=$assocName][1]/following-sibling::mif:participantClass/mif:class/mif:association[mif:targetConnection/@name=local-name(current())]/@sortKey" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
								<xsl:when test="local-name(current())='consumable' or local-name(current())='product' ">
									<xsl:sequence select="'1.5'" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:sequence select="'9'" />
								</xsl:otherwise>
							</xsl:choose>
							</xsl:otherwise>
						</xsl:choose> 	
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of
							select="document('SerialisedCDAModel.xml')//mif:targetConnection[@name=$assocName]/mif:participantClass/mif:class/mif:association[mif:targetConnection/@name=local-name(current())]/@sortKey" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:sort>
			<xsl:copy>
				<xsl:copy-of select="@*"></xsl:copy-of>
				<xsl:call-template name="sortChildren"></xsl:call-template>
			</xsl:copy>
		</xsl:for-each>
	</xsl:template>
	
	<!-- reorder in alphabetical order for Generic CDA  -> templated CDA-like -->
	<xsl:template match="h:*|npfitlc:*" mode="alpha-order">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:copy-of select="text()"/>
			<xsl:apply-templates select="*[not(@typeCode) and not(@classCode) and not(local-name(.) = 'languageCommunication')]" mode="alpha-order">
				<xsl:sort select="local-name(.)"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="*[@typeCode|@classCode or local-name(.) = 'languageCommunication']" mode="alpha-order">
				<xsl:sort select="local-name(.)"/>
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	
	<!-- identity template for complex datatype elements, which should not be reordered-->
	<!-- TODO: list all compont DTs here-->
	<xsl:template match="h:tel|h:value|h:effectiveTime|h:code|h:name|h:time|h:text|h:addr" mode="alpha-order">
        <xsl:copy-of select="."/>
    </xsl:template>
    
	<!-- Identity template for everything else-->
    <xsl:template match="node()|@*" mode="alpha-order">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" mode="alpha-order"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
