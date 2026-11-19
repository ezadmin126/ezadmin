if (isNotBlank("ID")) {
    return selectOne("SELECT ID, ASSET_NAME, NOTES FROM DEMO_ASSET WHERE ID = #{ID} AND DELETED = FALSE");
}
