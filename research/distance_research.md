# Distance research
Problem: How to calculate distance between 2 spaces on a grid

## Experimentation

Example array: {{0,1,2},{3,4,5},{6,7,8}}

0 1 2  
3 4 5  
6 7 8  

Array Positions:  
00 01 02  
10 11 12  
20 21 22  

## Testing:  
11 to 00:  
- (1+1)-(0+0) = 2  

00 to 11:  
- (0+0)-(1+1) = -2  
- |(0+0)-(1+1)| = 2  

00 to 01:  
- |(0+0)-(0+1)| = 1  

20 to 01:  
- |(2+0)-(0+1)| = 1 (This should not work)  

## Manhatten distance
"The sum of the horizontal and vertical distances between points on a grid"  
(1X-2X)+(1Y-2Y)

20 to 01:  
- |(2-0)+(0-1)| = 1 (This should not work)  
- |(2-0)|+|(0-1)| = 3