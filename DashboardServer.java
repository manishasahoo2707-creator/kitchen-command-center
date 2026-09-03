import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;

// --- CUSTOMER & ORDER DATA MODEL ---
class KitchenOrder implements Comparable<KitchenOrder> {
    String orderId;
    String customerId;
    String customerName;
    String itemDetails;
    int priority; // 1 = Express/VIP, 5 = Standard

    public KitchenOrder(String orderId, String customerId, String customerName, String itemDetails, int priority) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.itemDetails = itemDetails;
        this.priority = priority;
    }

    @Override
    public int compareTo(KitchenOrder other) {
        return Integer.compare(this.priority, other.priority);
    }
}

public class DashboardServer {

    // --- CORE DATA STRUCTURES ---
    private static PriorityQueue<KitchenOrder> priorityQueue = new PriorityQueue<>();
    private static Stack<String> assemblyStack = new Stack<>();
    private static Queue<String> driveThruQueue = new LinkedList<>();
    private static Map<String, String> loyaltyMap = new HashMap<>();
    private static List<String> activityLog = new ArrayList<>();
    
    private static int totalServed = 0;

    public static void main(String[] args) throws IOException {
        // Sample Initial State
        priorityQueue.add(new KitchenOrder("ORD-102", "CUST-8812", "Alex Vance", "UberEats Express - 2x Burgers", 1));
        priorityQueue.add(new KitchenOrder("ORD-105", "CUST-4021", "Sarah Connor", "Dine-In Family Meal", 5));

        assemblyStack.push("1. Toasted Bottom Bun");
        assemblyStack.push("2. Double Wagyu Beef & Cheddar");
        assemblyStack.push("3. Crispy Onions & House Sauce");

        driveThruQueue.add("Car 1: Red Sedan (CUST-9011 - #ORD-101)");
        driveThruQueue.add("Car 2: Blue SUV (CUST-3329 - #ORD-102)");

        loyaltyMap.put("555-0199", "CUST-8812 | Alex Vance | 450 Pts (GOLD) | Fav: Double Wagyu");
        loyaltyMap.put("555-0244", "CUST-4021 | Sarah Connor | 120 Pts (SILVER) | Fav: Family Combo");

        logAction("SYSTEM", "SYS-0000", "Dashboard initialized on Port 8085 with live Data Structures.");

        // Port set to 8085 to avoid conflicts
        HttpServer server = HttpServer.create(new InetSocketAddress(8085), 0);
        server.createContext("/", new DashboardHandler());
        server.createContext("/api/action", new ActionHandler());
        server.setExecutor(null);

        System.out.println("==========================================================");
        System.out.println("⚡ ENTERPRISE DASHBOARD RUNNING!");
        System.out.println("👉 Open browser: http://localhost:8085");
        System.out.println("==========================================================");

        server.start();
    }

    private static void logAction(String tag, String custId, String message) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        activityLog.add("[" + time + "] [" + tag + "] [" + custId + "] " + message);
    }

    // --- HTML DASHBOARD RENDERER ---
    static class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder html = new StringBuilder();

            html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>")
                .append("<meta http-equiv='refresh' content='3'>") // Auto refresh every 3s
                .append("<title>Kitchen Command Dashboard</title>")
                .append("<style>")
                .append("* { box-sizing: border-box; font-family: 'Segoe UI', Inter, sans-serif; }")
                .append("body { background-color: #0b1120; color: #f8fafc; margin: 0; padding: 20px; }")
                
                // Top Header
                .append(".header { display: flex; justify-content: space-between; align-items: center; background: #1e293b; padding: 15px 25px; border-radius: 12px; border-bottom: 3px solid #3b82f6; margin-bottom: 20px; box-shadow: 0 4px 15px rgba(0,0,0,0.3); }")
                .append(".header h1 { margin: 0; font-size: 22px; color: #38bdf8; display: flex; align-items: center; gap: 10px; }")
                .append(".badge { background: #0284c7; color: white; padding: 4px 10px; border-radius: 20px; font-size: 11px; }")
                
                // Metrics Cards
                .append(".metrics-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 15px; margin-bottom: 20px; }")
                .append(".metric-card { background: #1e293b; padding: 16px; border-radius: 10px; border-left: 5px solid #3b82f6; transition: transform 0.2s; }")
                .append(".metric-title { font-size: 11px; text-transform: uppercase; color: #94a3b8; font-weight: 600; letter-spacing: 0.5px; }")
                .append(".metric-value { font-size: 28px; font-weight: bold; margin-top: 6px; }")
                
                // Content Columns
                .append(".columns-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 15px; margin-bottom: 20px; }")
                .append(".column-box { background: #1e293b; border-radius: 10px; border: 1px solid #334155; overflow: hidden; display: flex; flex-direction: column; }")
                .append(".column-header { padding: 12px 15px; font-weight: bold; font-size: 13px; color: #fff; text-align: left; display: flex; justify-content: space-between; }")
                .append(".bg-red { background: linear-gradient(90deg, #ef4444, #dc2626); }")
                .append(".bg-amber { background: linear-gradient(90deg, #f59e0b, #d97706); }")
                .append(".bg-blue { background: linear-gradient(90deg, #3b82f6, #2563eb); }")
                .append(".bg-purple { background: linear-gradient(90deg, #a855f7, #9333ea); }")
                .append(".column-body { padding: 12px; list-style: none; margin: 0; max-height: 280px; overflow-y: auto; flex-grow: 1; }")
                .append(".column-body li { background: #0f172a; padding: 10px 12px; border-radius: 6px; margin-bottom: 8px; font-size: 12px; border-left: 3px solid #475569; }")
                
                // Explanatory DS Panel
                .append(".ds-panel { background: #1e293b; padding: 15px; border-radius: 10px; margin-bottom: 20px; border: 1px solid #334155; display: grid; grid-template-columns: repeat(4, 1fr); gap: 15px; text-align: center; }")
                .append(".ds-card { background: #0f172a; padding: 10px; border-radius: 6px; font-size: 11px; }")
                .append(".ds-title { color: #38bdf8; font-weight: bold; margin-bottom: 4px; }")
                
                // Action Buttons
                .append(".controls-bar { background: #1e293b; padding: 16px; border-radius: 10px; text-align: center; border: 1px solid #334155; }")
                .append(".btn { background: #3b82f6; color: white; border: none; padding: 11px 20px; font-weight: bold; border-radius: 8px; cursor: pointer; margin: 0 6px; font-size: 12px; text-decoration: none; display: inline-block; }")
                .append(".btn-red { background: #ef4444; } .btn-green { background: #22c55e; } .btn-purple { background: #a855f7; }")
                .append("</style></head><body>");

            // Header
            html.append("<div class='header'>")
                .append("<h1>⚡ FAST-FOOD KITCHEN COMMAND CENTER <span class='badge'>v2.0 Advanced</span></h1>")
                .append("<div><span style='color:#22c55e; font-weight:bold; font-size:13px;'>🟢 SYSTEM ONLINE</span></div>")
                .append("</div>");

            // Metric Cards
            html.append("<div class='metrics-grid'>")
                .append("<div class='metric-card' style='border-color:#ef4444;'><div class='metric-title'>PriorityQueue Pending</div><div class='metric-value' style='color:#ef4444;'>").append(priorityQueue.size()).append("</div></div>")
                .append("<div class='metric-card' style='border-color:#f59e0b;'><div class='metric-title'>Assembly Stack Layers</div><div class='metric-value' style='color:#f59e0b;'>").append(assemblyStack.size()).append("</div></div>")
                .append("<div class='metric-card' style='border-color:#3b82f6;'><div class='metric-title'>Drive-Thru Queue</div><div class='metric-value' style='color:#3b82f6;'>").append(driveThruQueue.size()).append("</div></div>")
                .append("<div class='metric-card' style='border-color:#22c55e;'><div class='metric-title'>Total Meals Served</div><div class='metric-value' style='color:#22c55e;'>").append(totalServed).append("</div></div>")
                .append("</div>");

            // Data Structure Explanation Banner (Judge Feature)
            html.append("<div class='ds-panel'>")
                .append("<div class='ds-card'><div class='ds-title'>PriorityQueue</div>Min-Heap | O(log N) Priority</div>")
                .append("<div class='ds-card'><div class='ds-title'>Stack</div>LIFO Burger Prep | O(1) Push/Pop</div>")
                .append("<div class='ds-card'><div class='ds-title'>Queue</div>FIFO Lane Traffic | O(1) Enqueue</div>")
                .append("<div class='ds-card'><div class='ds-title'>HashMap</div>O(1) Instant Customer Lookup</div>")
                .append("</div>");

            // Columns
            html.append("<div class='columns-grid'>");

            // 1. Priority Queue
            html.append("<div class='column-box'><div class='column-header bg-red'><span>🚨 PriorityQueue (Orders)</span><span>O(log N)</span></div><ul class='column-body'>");
            List<KitchenOrder> tempQueue = new ArrayList<>(priorityQueue);
            Collections.sort(tempQueue);
            for (KitchenOrder o : tempQueue) {
                html.append("<li style='border-left-color:#ef4444;'><b>[").append(o.orderId).append("]</b> Priority ").append(o.priority)
                    .append("<br><span style='color:#38bdf8;'>Cust: ").append(o.customerId).append(" (").append(o.customerName).append(")</span>")
                    .append("<br><small style='color:#94a3b8;'>").append(o.itemDetails).append("</small></li>");
            }
            html.append("</ul></div>");

            // 2. Stack
            html.append("<div class='column-box'><div class='column-header bg-amber'><span>🥩 Assembly Stack</span><span>LIFO</span></div><ul class='column-body'>");
            for (int i = assemblyStack.size() - 1; i >= 0; i--) {
                html.append("<li style='border-left-color:#f59e0b;'>").append(assemblyStack.get(i))
                    .append(i == assemblyStack.size() - 1 ? " <b style='color:#f59e0b;'>(TOP LAYER)</b>" : "")
                    .append("</li>");
            }
            html.append("</ul></div>");

            // 3. Queue
            html.append("<div class='column-box'><div class='column-header bg-blue'><span>🚗 Drive-Thru Queue</span><span>FIFO</span></div><ul class='column-body'>");
            for (String car : driveThruQueue) {
                html.append("<li style='border-left-color:#3b82f6;'>").append(car).append("</li>");
            }
            html.append("</ul></div>");

            // 4. Detailed Customer Activity Log
            html.append("<div class='column-box'><div class='column-header bg-purple'><span>📜 Customer Activity Log</span><span>LIVE</span></div><ul class='column-body'>");
            for (int i = activityLog.size() - 1; i >= 0; i--) {
                html.append("<li style='border-left-color:#a855f7;'>").append(activityLog.get(i)).append("</li>");
            }
            html.append("</ul></div>");

            html.append("</div>");

            // Control Buttons
            html.append("<div class='controls-bar'>")
                .append("<a href='/api/action?cmd=push' class='btn btn-red'>+ Add Express VIP Order</a>")
                .append("<a href='/api/action?cmd=poll' class='btn btn-green'>🍔 Cook & Poll Priority Ticket</a>")
                .append("<a href='/api/action?cmd=enqueue' class='btn'>🚗 Vehicle Arrives</a>")
                .append("<a href='/api/action?cmd=lookup' class='btn btn-purple'>🔍 Lookup Customer (HashMap)</a>")
                .append("</div>");

            html.append("</body></html>");

            byte[] response = html.toString().getBytes();
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    // --- INTERACTIVE API HANDLER ---
    static class ActionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();

            if (query != null) {
                if (query.contains("cmd=push")) {
                    int randId = 1000 + new Random().nextInt(9000);
                    String custId = "CUST-" + randId;
                    String ordId = "ORD-" + (106 + priorityQueue.size());
                    
                    priorityQueue.add(new KitchenOrder(ordId, custId, "VIP Member", "Express Combo Meal", 1));
                    logAction("EXPRESS_PUSH", custId, "Added VIP Ticket " + ordId + " to PriorityQueue [O(log N)].");

                } else if (query.contains("cmd=poll")) {
                    if (!priorityQueue.isEmpty()) {
                        KitchenOrder polled = priorityQueue.poll();
                        totalServed++;
                        logAction("POLL_SERVED", polled.customerId, "Prepared & Served " + polled.orderId + " (" + polled.customerName + ").");
                    } else {
                        logAction("SYSTEM", "CUST-NONE", "Poll attempted: Priority Queue is empty!");
                    }

                } else if (query.contains("cmd=enqueue")) {
                    int randId = 1000 + new Random().nextInt(9000);
                    String custId = "CUST-" + randId;
                    String car = "Car " + (driveThruQueue.size() + 1) + ": (" + custId + ")";
                    driveThruQueue.add(car);
                    logAction("DRIVE_THRU", custId, "Vehicle enqueued into Drive-Thru Lane [FIFO].");

                } else if (query.contains("cmd=lookup")) {
                    String info = loyaltyMap.get("555-0199");
                    logAction("HASHMAP_SCAN", "CUST-8812", "Instant Lookup Result: " + info);
                }
            }

            // Redirect back to main dashboard page
            exchange.getResponseHeaders().set("Location", "/");
            exchange.sendResponseHeaders(302, -1);
        }
    }
}
