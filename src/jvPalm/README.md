jvPalm v2 (Nov 25)
 - change Snowflake ORG-EP: atixoaj > skbsfog
   - jx75304.ap-northeast-2.aws.snowflakecomputing.com
   - skbsfog-skbroadband.snowflakecomputing.com
   - jx75304.ap-northeast-2.privatelink.snowflakecomputing.com
   - skbsfog-skbroadband.privatelink.snowflakecomputing.com
  - use Snowflake Key-pair JDBC connection string
 - change name: mvSFM_QueryHistory > mvSFQH

jvPalm (~Oct 25)
 - Mysql JDBC connection test
 - Snowflake JDBC connection test
 - move Snowflake.QueryHistory to mysql

environments
- only source jar package
- excluded jar library (u must control jar libs)
- IDPWD based JDBC connection string
+==