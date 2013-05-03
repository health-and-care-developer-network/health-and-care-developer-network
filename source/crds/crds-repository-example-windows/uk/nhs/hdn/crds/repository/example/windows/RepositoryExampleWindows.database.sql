USE [master]
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'crds')
BEGIN
CREATE DATABASE [crds] ON  PRIMARY
( NAME = N'crds', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL10_50.MSSQLSERVER\MSSQL\DATA\crds.mdf' , SIZE = 3072KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON
( NAME = N'crds_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL10_50.MSSQLSERVER\MSSQL\DATA\crds_log.ldf' , SIZE = 1024KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
 COLLATE Latin1_General_CI_AS
END
GO
ALTER DATABASE [crds] SET COMPATIBILITY_LEVEL = 100
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [crds].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
USE [crds]
GO

-- SPLIT

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[patients]') AND type in (N'U'))
DROP TABLE [dbo].[patients]
GO
IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_events_repository_event_identifier]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[events] DROP CONSTRAINT [DF_events_repository_event_identifier]
END
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[events]') AND type in (N'U'))
DROP TABLE [dbo].[events]
GO
USE [crds]
GO

-- SPLIT

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[events]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[events](
	[provider_identifier] [uniqueidentifier] NOT NULL,
	[repository_identifier] [uniqueidentifier] NOT NULL,
	[stuff_identifier] [uniqueidentifier] NOT NULL,
	[patient_identifier_nhs_number] [nchar](10) COLLATE Latin1_General_CI_AS NOT NULL,
	[stuff_event_set_identifier] [uniqueidentifier] NOT NULL,
	[stuff_event_identifier] [uniqueidentifier] ROWGUIDCOL NOT NULL CONSTRAINT [DF_events_stuff_event_identifier]  DEFAULT (newid()),
	[stuff_event_timestamp] [datetime] NOT NULL CONSTRAINT [DF_events_stuff_event_timestamp] DEFAULT (GETUTCDATE()),
	[stuff_event_kind] [nvarchar](10) COLLATE Latin1_General_CI_AS NOT NULL
) ON [PRIMARY]
END
GO

-- SPLIT

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[patients]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[patients](
	[patient_identifier_nhs_number] [nchar](10) COLLATE Latin1_General_CI_AS NOT NULL,
	[firstname] [nvarchar](32) COLLATE Latin1_General_CI_AS NULL,
	[lastname] [nvarchar](32) COLLATE Latin1_General_CI_AS NULL,
	[address] [nvarchar](64) COLLATE Latin1_General_CI_AS NULL
) ON [PRIMARY]
END
GO

-- SPLIT

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(N'[dbo].[patient_inserted]'))
EXEC dbo.sp_executesql @statement = N'-- =============================================
CREATE TRIGGER [dbo].[patient_inserted]
   ON  [dbo].[patients]
   AFTER INSERT
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @provider_identifier uniqueidentifier;
	SET @provider_identifier = ''b58a74c0-0b91-40d9-b460-d82d13c68b90'';
	DECLARE @repository_identifier uniqueidentifier;
	SET @repository_identifier = ''b09f747e-a372-4a00-b349-c6ca7f663ad4'';
	DECLARE @stuff_identifier uniqueidentifier
	SET @stuff_identifier = ''c22d8702-f8ca-4f2f-836a-b75ad69a4231'';

	DECLARE @stuff_event_set_identifier uniqueidentifier
	SET @stuff_event_set_identifier = NEWID();

	INSERT INTO dbo.events
	(
		provider_identifier,
		repository_identifier,
		stuff_identifier,
		patient_identifier_nhs_number,
		stuff_event_set_identifier,
		stuff_event_kind
	)
	SELECT @provider_identifier, @repository_identifier, @stuff_identifier, patient_identifier_nhs_number, @stuff_event_set_identifier, ''Inserted''
	FROM inserted

END
'
GO

-- SPLIT

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(N'[dbo].[patient_updated]'))
EXEC dbo.sp_executesql @statement = N'-- =============================================
CREATE TRIGGER [dbo].[patient_updated]
   ON  [dbo].[patients]
   AFTER UPDATE
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @provider_identifier uniqueidentifier;
	SET @provider_identifier = ''b58a74c0-0b91-40d9-b460-d82d13c68b90'';
	DECLARE @repository_identifier uniqueidentifier;
	SET @repository_identifier = ''b09f747e-a372-4a00-b349-c6ca7f663ad4'';
	DECLARE @stuff_identifier uniqueidentifier
	SET @stuff_identifier = ''c22d8702-f8ca-4f2f-836a-b75ad69a4231'';

	DECLARE @stuff_event_set_identifier uniqueidentifier
	SET @stuff_event_set_identifier = NEWID();

	INSERT INTO dbo.events
	(
		provider_identifier,
		repository_identifier,
		stuff_identifier,
		patient_identifier_nhs_number,
		stuff_event_set_identifier,
		stuff_event_kind
	)
	SELECT @provider_identifier, @repository_identifier, @stuff_identifier, patient_identifier_nhs_number, @stuff_event_set_identifier, ''Updated''
	FROM inserted

END
'
GO

-- SPLIT

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(N'[dbo].[patient_deleted]'))
EXEC dbo.sp_executesql @statement = N'-- =============================================
CREATE TRIGGER [dbo].[patient_deleted]
   ON  [dbo].[patients]
   AFTER DELETE
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @provider_identifier uniqueidentifier;
	SET @provider_identifier = ''b58a74c0-0b91-40d9-b460-d82d13c68b90'';
	DECLARE @repository_identifier uniqueidentifier;
	SET @repository_identifier = ''b09f747e-a372-4a00-b349-c6ca7f663ad4'';
	DECLARE @stuff_identifier uniqueidentifier
	SET @stuff_identifier = ''c22d8702-f8ca-4f2f-836a-b75ad69a4231'';

	DECLARE @stuff_event_set_identifier uniqueidentifier
	SET @stuff_event_set_identifier = NEWID();

	INSERT INTO dbo.events
	(
		provider_identifier,
		repository_identifier,
		stuff_identifier,
		patient_identifier_nhs_number,
		stuff_event_set_identifier,
		stuff_event_kind
	)

	SELECT @provider_identifier, @repository_identifier, @stuff_identifier, patient_identifier_nhs_number, @stuff_event_set_identifier, ''Deleted''
	FROM deleted

END
'
GO







INSERT INTO dbo.patients
(
	[patient_identifier_nhs_number],
	[firstname],
	[lastname],
	[address]
)
VALUES
(
	'1234567880',
	'Raph',
	'Cohn',
	'Skipton'
)
GO

UPDATE dbo.patients
SET [address] = 'Bradford'
GO

DELETE FROM dbo.patients
GO


