```dbml
Table the_user{
  id bigint [primary key]
  id_init_capital bigint
  username varchar(60) [unique]
  nickname varchar(60)
  password varchar(255)
}

Table user_role{
  id_user bigint [primary key]
  id_roles bigint [primary key]
}

Table the_role{
  id bigint [primary key]
  name varchar(10) [unique]
}

Table the_init_capital{
  id bigint [primary key]
  init_value bigdecimal
  created Date
}

Table the_periods{
  id bigint [primary key]
  id_user bigint
  created varchar(60)
}

Table fixed_costs{
  id bigint [primary key]
  id_period bigint
  date date
  value bigdecimal
}

Table fixed_income{
  id bigint [primary key]
  id_period bigint
  date date
  value bigdecimal
}

Table variable_costs{
  id bigint [primary key]
  id_period bigint
  date date
  value bigdecimal
}

Table variable_income{
  id bigint [primary key]
  id_period bigint
  date date
  value bigdecimal
}

REF : the_user.id < user_role.id_user
REF : the_role.id < user_role.id_roles
REF : the_user.id < the_periods.id_user
Ref : the_init_capital.id < the_user.id_init_capital
REF : the_periods.id < fixed_costs.id_period
REF : the_periods.id < fixed_income.id_period
REF : the_periods.id < variable_costs.id_period
REF : the_periods.id < variable_income.id_period
```