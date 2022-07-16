package servlet;

import DAO.CarrinhoDao;
import model.Produto;
import services.CarrinhoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/carrinho")
public class CarrinhoServlet extends HttpServlet {   
	private static final long serialVersionUID = 1L;

    private double valorTotal;
    private List<Produto> listProdutosCarrinho= new ArrayList<>();

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String paramDel = request.getParameter("del");
        String paramAdd=request.getParameter("add");

         System.out.println("del "+ paramDel);
         System.out.println("add "+paramAdd);

        if (paramDel== null && paramAdd== null){
            try{
                CarrinhoDao carrinhoDao =  new CarrinhoDao();
                carrinhoDao.adicionaCarrinho(listProdutosCarrinho);
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
            request.setAttribute("produtos", listProdutosCarrinho);
            request.getRequestDispatcher("/WEB-INF/views/carrinho.jsp").forward(request,response);
        }else{
            if(paramDel== null){
                String id = request.getParameter("add");
                int idProd = Integer.parseInt(id);

                Produto produto = new CarrinhoService().addProdutoCarrinho(idProd);
                valorTotal+= produto.getPreco();
                this.listProdutosCarrinho.add(new Produto(produto.getID(), produto.getNome(), produto.getDesc(), produto.getPreco(), 0, produto.getValidade(), valorTotal));

                response.sendRedirect("/blueSupermarket/produtos");
            }else{
                String id = request.getParameter("del");
                int idDel = Integer.parseInt(id);
                Produto produtos = new CarrinhoService().addProdutoCarrinho(idDel);
                int index = 0;
                for (Produto produto: listProdutosCarrinho) {
                    if(produto.getID() == idDel){
                        index = listProdutosCarrinho.indexOf(produto);
                    }
                }
                System.out.println(index);
                listProdutosCarrinho.remove(index);

                request.setAttribute("produtos", listProdutosCarrinho);
                request.getRequestDispatcher("/WEB-INF/views/carrinho.jsp").forward(request,response);
            }

        }

    }
}