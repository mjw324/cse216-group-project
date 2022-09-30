
var describe: any;
var it: any;
var expect: any;

//Took this form the unit testing tutorial, a very good structure to test if
//likes and dislikes are actually accounted for in the database
describe("Tests of if like counter works", function() {
    
    it("Adding 1 should work", function() {
        var foo = 0;
        foo += 1;
        expect(foo).toEqual(1);
    });

    it("Subtracting 1 should work", function () {
        var foo = 0;
        foo -= 1;
        expect(foo).toEqual(-1);
    });
});

describe("Tests of connectiona and data collection", function() {
    it("Connection to Postgres works", function() {
        // This is where code will be added to see if the connection to postgres is right
    });

    it("Data IS being collected in the database", function () {
        // This is where code will be added to see if the database is actually 
        // Getting populated with data and posts from our app
    });
});