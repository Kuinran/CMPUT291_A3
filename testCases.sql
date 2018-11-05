insert into members (email, name, phone, pwd)
  values ('jarrett@ualberta.ca', 'jarrett', '999-999-9999', 'password');

insert into members (email, name, phone, pwd)
  values ('jim@gmail.com', 'jim', '999-999-9999', 'password');

insert into members (email, name, phone, pwd)
  values ('email@email.com', 'email', '999-999-9999', 'password');

insert into members (email, name, phone, pwd)
  values ('fakenews@news.com', 'human', '999-999-9999', 'password');

insert into cars (cno, make, model, year, seats, owner)
  values ('1', 'Acura', 'MDX', '2006', '5', 'jim@gmail.com');

insert into cars (cno, make, model, year, seats, owner)
  values ('2', 'Ford', 'F150', '2018', '5', 'email@email.com');

insert into locations (lcode, city, prov, address)
  values ('ab1', 'calgary', 'alberta', 'fakeaddress');

insert into locations (lcode, city, prov, address)
  values ('ab2', 'edmonton', 'alberta', 'realaddress');

insert into locations (lcode, city, prov, address)
  values ('bc1', 'vancouver', 'british columbia', 'hastings');

insert into rides (rno, price, rdate, seats, lugDesc, src, dst, driver, cno)
  values ('1', '100', '2019-01-01', '5', 'stuff', 'ab1', 'bc1',
    'email@email.com', '2');

insert into rides (rno, price, rdate, seats, lugDesc, src, dst, driver, cno)
  values ('2', '50', '2019-02-02', '3', 'stuff', 'ab2', 'ab1',
    'jim@gmail.com', '1');

insert into rides (rno, price, rdate, seats, lugDesc, src, dst, driver)
  values ('3', '50', '2019-03-02', '6', 'stuff', 'ab1', 'ab2',
    'fakenews@news.com');

insert into rides (rno, price, rdate, seats, lugDesc, src, dst, driver)
  values ('4', '50', '2019-03-05', '6', 'stuff', 'ab1', 'ab2',
    'fakenews@news.com');

insert into rides (rno, price, rdate, seats, lugDesc, src, dst, driver)
  values ('5', '50', '2019-03-12', '6', 'stuff', 'ab1', 'ab2',
    'fakenews@news.com');

insert into rides (rno, price, rdate, seats, lugDesc, src, dst, driver)
  values ('6', '50', '2019-03-06', '6', 'stuff', 'ab1', 'ab2',
    'fakenews@news.com');

insert into bookings (bno, email, rno, cost, seats, pickup, dropoff)
  values ('1', 'jarrett@ualberta.ca', '1', '80', '2', 'ab2', 'bc1');

insert into enroute (rno, lcode) values ('1', 'ab2');

insert into requests (rid, email, rdate, pickup, dropoff, amount)
  values ('1', 'jim@gmail.com', '2018-12-12', 'bc1', 'ab1', '100');
