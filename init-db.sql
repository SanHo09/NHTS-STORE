USE [master];
GO
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = "nhts-store")
  CREATE DATABASE [nhts-store];
GO