package com.kumuluz.ee.samples.graphql_security;

import com.kumuluz.ee.graphql.GraphQLApplication;
import com.kumuluz.ee.graphql.annotations.GraphQLApplicationClass;

import javax.annotation.security.DeclareRoles;

@GraphQLApplicationClass
@DeclareRoles({"user", "admin"})
public class CustomerApp extends GraphQLApplication {
}
