import '../styles/Pagination.css';

/**
 * Componente de paginação para a lista de tarefas.
 */
const Pagination = ({ 
    currentPage, 
    totalPages, 
    totalElements,
    paginated,
    onPageChange 
}) => {
    if (!paginated) {
        return null;
    }

    const getPageNumbers = () => {
        const pages = [];
        const maxVisible = 5;
        let start = Math.max(0, currentPage - Math.floor(maxVisible / 2));
        let end = Math.min(totalPages - 1, start + maxVisible - 1);

        // Ajusta inicio se estiver no final
        if (end - start < maxVisible - 1) {
            start = Math.max(0, end - maxVisible + 1);
        }

        for (let i = start; i <= end; i++) {
            pages.push(i);
        }

        return pages;
    };


    const handlePrevious = () => {
        if (currentPage > 0) {
            onPageChange(currentPage - 1);
        }
    };

    const handleNext = () => {
        if (currentPage < totalPages - 1) {
            onPageChange(currentPage + 1);
        }
    };


    return (
        <div className="pagination">
            <div className="pagination-info">
                <span>Página {currentPage + 1} de {totalPages}</span>
                <span className="pagination-total">({totalElements} tarefas)</span>
            </div>

            <div className="pagination-controls">
                <button 
                    className="pagination-btn"
                    onClick={handlePrevious}
                    disabled={currentPage === 0}
                    aria-label="Página anterior"
                >
                    Anterior
                </button>

                <div className="pagination-pages">
                    {getPageNumbers().map(page => (
                        <button
                            key={page}
                            className={`pagination-page ${page === currentPage ? 'active' : ''}`}
                            onClick={() => onPageChange(page)}
                        >
                            {page + 1}
                        </button>
                    ))}
                </div>

                <button 
                    className="pagination-btn"
                    onClick={handleNext}
                    disabled={currentPage >= totalPages - 1}
                    aria-label="Próxima página"
                >
                    Próxima
                </button>
            </div>
        </div>
    );
};

export default Pagination;
